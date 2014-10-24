package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.ReturnStatement;
import com.thesis.block.Statement;
import com.thesis.common.Util;
import com.thesis.expression.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator {

	private final MethodNode mMethod;
	private  Map<Integer, LocalVariable> mLocalVariables;
	Stack<Expression> mStack;
	List<Block> mStatements;
	StringBuffer buf;
	int mCurrentLine;

	public InstructionTranslator(MethodNode method, List<Block> statements, Map<Integer, LocalVariable> arguments) {
		buf = new StringBuffer();
		mStatements = statements;
		mStack = new Stack<>();
		mMethod = method;
		mLocalVariables = new HashMap<>();
		copyLocalVariables();
		mLocalVariables.putAll(arguments);
	}

	private void copyLocalVariables() {
		if(mMethod.localVariables.size() > 0) {
			for (Object var : mMethod.localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mLocalVariables.put(variable.index, new LocalVariable(variable));
			}
		}
	}

	public void addCode() {
		System.out.println("METHOD: " + mMethod.name);
		System.out.println(" ");
		AbstractInsnNode node = mMethod.instructions.getFirst();
		while (node != null) {
			node = visitCorrectNode(node);
			node = node.getNext();
		}
		addLocalVariablesAssignments();
	}

	private AbstractInsnNode visitCorrectNode(AbstractInsnNode node) {
		switch (node.getType()) {
			case AbstractInsnNode.INSN:
				visitInsnNode((InsnNode) node);
				break;
			case AbstractInsnNode.INT_INSN:
				visitIntInsnNode((IntInsnNode) node);
				break;
			case AbstractInsnNode.VAR_INSN:
				visitVarInsnNode((VarInsnNode) node);
				break;
			case AbstractInsnNode.TYPE_INSN:
				visitTypeInsnNode((TypeInsnNode) node);
				break;
			case AbstractInsnNode.FIELD_INSN:
				visitFieldInsnNode((FieldInsnNode) node);
				break;
			case AbstractInsnNode.METHOD_INSN:
				visitMethodInsnNode((MethodInsnNode) node);
				break;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				visitInvokeDynamicInsnNode((InvokeDynamicInsnNode) node);
				break;
			case AbstractInsnNode.JUMP_INSN:
				node = visitJumpInsnNode((JumpInsnNode) node);
				break;
			case AbstractInsnNode.LABEL:
				visitLabelNode((LabelNode) node);
				break;
			case AbstractInsnNode.LDC_INSN:
				visitLdcInsnNode((LdcInsnNode) node);
				break;
			case AbstractInsnNode.IINC_INSN:
				if (visitIincInsnNode((IincInsnNode) node))
					node = node.getNext(); //skip the following load instruction
				break;
			case AbstractInsnNode.TABLESWITCH_INSN:
				visitTableSwitchInsnNode((TableSwitchInsnNode) node);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				visitMultiANewArrayInsnNode((MultiANewArrayInsnNode) node);
				break;
			case AbstractInsnNode.FRAME:
				visitFrameNode((FrameNode) node);
				break;
			case AbstractInsnNode.LINE:
				visitLineNumberNode((LineNumberNode) node);
				break;
			default:
				printNodeInfo(node);
		}
		return node;
	}

	private void addLocalVariablesAssignments() {
		List<Block> localVars = mLocalVariables.values()
				.stream()
				.filter(variable -> !variable.isArgument())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable)))
				.collect(Collectors.toList());
		mStatements.addAll(0, localVars);
	}

	/**
	 *            NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
	 *            LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1,
	 *            IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
	 *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
	 *            POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP,
	 *            IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
	 *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
	 *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
	 *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR,
	 *            I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
	 *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
	 *            IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
	 *            ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
	 */
	private void visitInsnNode(InsnNode node) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.contains("LOAD")) {
			Expression index = mStack.pop();
			Expression arrayRef = mStack.pop();
			mStack.push(new ArrayAccessExpression(index, arrayRef));
		}
		if (opCode.contains("CONST")) {
			mStack.push(new PrimaryExpression(node));
		}
		if (opCode.contains("RETURN")) {
			if (mStack.size() == 1) mStatements.add(new ReturnStatement(mStack.pop()));
		}
		if (mStack.size() >= 2 && node.getOpcode() <= 131 && node.getOpcode() >= 96 ) {
			Expression ex2 = mStack.pop();
			Expression ex1 = mStack.pop();
			mStack.push(new ArithmeticExpression(node, ex1, ex2));
		}
	}

	//	BIPUSH, SIPUSH or NEWARRAY
	private void visitIntInsnNode(IntInsnNode node) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		switch(opCode) {
			case "BIPUSH":
				mStack.push(new PrimaryExpression(node.operand,"int"));
				break;
			case "SIPUSH":
				mStack.push(new PrimaryExpression(node.operand,"short"));
				break;
			case "NEWARRAY":
				Expression lengthExp = mStack.pop();
				mStack.push(new ArrayCreationExpression(lengthExp, node.operand));
				break;
		}
	}

//	ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
	private void visitVarInsnNode(VarInsnNode node) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.endsWith("LOAD")) {
			LocalVariable var = mLocalVariables.get(node.var);
			mStack.push(new PrimaryExpression(var, var.getType()));
		}
		if (opCode.endsWith("STORE")) {
			if (!mLocalVariables.containsKey(node.var)) {
				mLocalVariables.put(node.var, new LocalVariable("var"+node.var, node.var));
			}
			LocalVariable localVar = mLocalVariables.get(node.var);
			Expression rightSide =  mStack.pop(); // todo array assignment and type
			if (!localVar.hasType()) {
				localVar.setType(rightSide.getType());
			}
			if (localVar.hasDebugType()) {
				rightSide.setType(localVar.getType());
			}
			LeftHandSide leftSide = new LeftHandSide(localVar, rightSide.getType());
			mStatements.add(new Statement(new AssignmentExpression(node, leftSide, rightSide)));
		}
	}

//	NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	private void visitTypeInsnNode(TypeInsnNode node) {
		printNodeInfo(node);
	}

//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	private void visitFieldInsnNode(FieldInsnNode node) {
		printNodeInfo(node);
	}

//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	private void visitMethodInsnNode(MethodInsnNode node) {
		printNodeInfo(node);
	}

//	INVOKEDYNAMIC
	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node) {
		printNodeInfo(node);
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
	 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 * GOTO, JSR.
	 */
	private AbstractInsnNode visitJumpInsnNode(JumpInsnNode node) {
		printNodeInfo(node);
		AbstractInsnNode nextNode = node;
		int opcode = node.getOpcode();
		if (opcode >= 159 && opcode <= 166) { //between IF_ICMPEQ and IF_ACMPNE
			Expression rightSide = mStack.pop();
			Expression leftSide = mStack.pop();
			mStack.push(new LogicalExpression(node, leftSide, rightSide));
			nextNode = node.getNext();
		}

		if (opcode >= 153 && opcode <= 158) { // between IFEQ and IFLE
			Expression leftSide = mStack.pop();
			Expression rightSide = new PrimaryExpression(0, "int");
			mStack.push(new LogicalExpression(node, leftSide, rightSide));
			nextNode = node.getNext();
		}

		if (Util.getOpcodeString(opcode).equals("GOTO")) {
//			mStack.pop();
			Label label = node.label.getLabel();
			do {
				nextNode = nextNode.getNext();
			} while (!(nextNode instanceof LabelNode) || !((LabelNode) nextNode).getLabel().equals(label));
		}

		System.out.println(node.label.getLabel());
		return nextNode;
	}

	private void visitLabelNode(LabelNode node) {
		printNodeInfo(node);
		Label l = node.getLabel();
		if (l != null) {
			System.out.println("Label: " + l);
//			try {
//				System.out.println("offset: " + l.getOffset());
//			} catch (IllegalStateException e) {
//				System.out.println("offset not resolved");
//			}
//			if (l.info != null)
//				System.out.println("info: " + l.info.toString());
		}
	}

	// LDC
	private void visitLdcInsnNode(LdcInsnNode node) {
		printNodeInfo(node);
		buf.append(" ").append(node.cst);
		String type;
		if (node.cst instanceof Integer) {
			type = "int";
		} else if (node.cst instanceof Float) {
			type = "float";
		} else if (node.cst instanceof Double) {
			type = "double";
		} else if (node.cst instanceof Long) {
			type = "long";
		} else if (node.cst instanceof String) {
			type = "String";
		} else {
			type = Util.getType(((Type) node.cst).getDescriptor());
		}
		mStack.push(new PrimaryExpression(node.cst, type));
	}

	private boolean visitIincInsnNode(IincInsnNode node) {
		printNodeInfo(node);
		LocalVariable variable = mLocalVariables.get(node.var);

		if (node.getPrevious() != null && node.getPrevious().getOpcode() == 21 ) {
			UnaryExpression unaryExpression = new UnaryExpression(node, variable, "int", UnaryExpression.OpPosition.POSTFIX);
			mStack.pop(); // remove loaded var from stack
			mStack.push(unaryExpression);
			return false;
		}
		if (node.getNext() != null && node.getNext().getOpcode() == 21 ) {
			UnaryExpression unaryExpression = new UnaryExpression(node, variable, "int", UnaryExpression.OpPosition.PREFIX);
			mStack.push(unaryExpression);
			return true;
		}
		AssignmentExpression expression = new AssignmentExpression(node, new LeftHandSide(variable, "int"), new PrimaryExpression(node.incr, "int"));
		mStatements.add(new Statement(expression));
		return false;
	}

//	TABLESWITCH
	private void visitTableSwitchInsnNode(TableSwitchInsnNode node) {
		printNodeInfo(node);
	}

//	MULTIANEWARRAY
	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node) {
		printNodeInfo(node);
	}

	private void visitFrameNode(FrameNode node) {
		System.out.println("FRAME:");
		printNodeInfo(node);
		System.out.println("local: " + Arrays.deepToString(node.local.toArray()));
		System.out.println("stack: " + Arrays.deepToString(node.stack.toArray()));
	}

	private void visitLineNumberNode(LineNumberNode node) {
		printNodeInfo(node);
		mCurrentLine = node.line;
		System.out.println("line: " + mCurrentLine + ", " + node.start.getLabel());

	}

	private void printNodeInfo(AbstractInsnNode node) {
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.isEmpty()) return;
		String fields = "";
		for (Field field : node.getClass().getFields()) {
			if (!(field.getName().contains("INSN") || field.getName().contains("LABEL")|| field.getName().contains("FRAME") || field.getName().contains("LINE"))) {
				try {
					fields += field.getName() + " = " + field.get(node);
					fields += "; ";
				} catch (IllegalAccessException e) {
					System.out.println(field.getName() + " is inaccessible");
				}
			}

		}
		String result = "code: " + opCode + " " + fields;
		System.out.println(result);
	}
}

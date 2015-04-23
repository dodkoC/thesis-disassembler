package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.LeftHandSide;
import com.thesis.expression.PrimaryExpression;
import com.thesis.expression.UnaryExpression;
import com.thesis.expression.stack.ExpressionStack;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.MethodState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;

import java.util.Map;

public class IincInsnNodeHandler extends AbstractHandler {

	private Map<Integer, LocalVariable> mLocalVariables;

	public IincInsnNodeHandler(MethodState state, Map<Integer, LocalVariable> localVariables) {
		super(state);
		mLocalVariables = localVariables;
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, IincInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		LocalVariable variable = mLocalVariables.get(((IincInsnNode)node).var);
		if (node.getPrevious() != null && node.getPrevious().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node.getOpcode(), variable, DataType.INT, UnaryExpression.OpPosition.POSTFIX));
			return;
		}
		if (node.getNext() != null && node.getNext().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node.getOpcode(), variable, DataType.INT, UnaryExpression.OpPosition.PREFIX));
			return;
		}
		stack.push(new AssignmentExpression(node.getOpcode(), new LeftHandSide(node.getOpcode(), variable),
				new PrimaryExpression(node.getOpcode(), ((IincInsnNode)node).incr, DataType.INT)));
	}
}

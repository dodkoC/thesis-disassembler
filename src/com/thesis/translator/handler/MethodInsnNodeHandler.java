package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.ConstructorInvocationExpression;
import com.thesis.expression.MethodInvocationExpression;
import com.thesis.expression.stack.ExpressionStack;
import com.thesis.translator.MethodState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodInsnNodeHandler extends AbstractHandler {

	private String mCallerMethodName;
	private DataType mEnclosingClassType;

	public MethodInsnNodeHandler(MethodState state, String callerMethodName, DataType enclosingClassType) {
		super(state);
		mCallerMethodName = callerMethodName;
		mEnclosingClassType = enclosingClassType;
	}

	//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, MethodInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		String name = ((MethodInsnNode)node).name;
		String owner = ((MethodInsnNode)node).owner;
		String desc = ((MethodInsnNode)node).desc;

		if (node.getOpcode() == Opcodes.INVOKESPECIAL && Util.isConstructor(name)) {
			stack.push(new ConstructorInvocationExpression(node.getOpcode(), name, desc, owner, mCallerMethodName, mEnclosingClassType));
		} else {
			stack.push(new MethodInvocationExpression(node.getOpcode(), name, desc, owner, mCallerMethodName));
		}
	}
}

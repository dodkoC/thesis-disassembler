package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.LambdaExpression;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

public class InvokeDynamicInsnNodeHandler extends AbstractHandler {

	public InvokeDynamicInsnNodeHandler(MethodState state) {
		super(state);
	}

	//	INVOKEDYNAMIC
	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, InvokeDynamicInsnNode.class);
		InvokeDynamicInsnNode invokeNode = (InvokeDynamicInsnNode) node;
		mState.getActiveStack().push(new LambdaExpression(invokeNode.name, invokeNode.desc, invokeNode.bsm, invokeNode.bsmArgs));
	}
}

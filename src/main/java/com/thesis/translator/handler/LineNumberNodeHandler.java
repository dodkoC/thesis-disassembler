package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

/**
 * Handles the {@link LineNumberNode}
 */
public class LineNumberNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(LineNumberNodeHandler.class);

	public LineNumberNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, LineNumberNode.class);

		mState.setCurrentLine(((LineNumberNode)node).line);
	}
}

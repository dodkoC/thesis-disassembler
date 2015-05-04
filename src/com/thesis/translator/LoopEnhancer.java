package com.thesis.translator;

import com.thesis.expression.*;

import java.util.List;
import java.util.Stack;

public class LoopEnhancer implements StackEnhancer {

	@Override
	public void enhance(ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack) {
		for (int i = 0; i < expressionStack.size(); i++) {
			Expression exp = expressionStack.get(i);
			if (exp instanceof JumpExpression) {
				updateDoLoops((JumpExpression) exp, i, expressionStack, stack);
			}
		}

		for (int i = 0; i < stack.size(); i++) {
			Expression exp = stack.get(i).getExpression();
			if (exp instanceof JumpExpression) {
				int loopStartLabel = stack.get(i).getLabelId();
				int nextLabel = i < stack.size() - 1 ? stack.get(i+1).getLabelId() : -2;
				fixLoopDetection((JumpExpression) exp, loopStartLabel, stack.get(i));
				improveBreakAndContinue((JumpExpression) exp, nextLabel, (JumpExpression) exp);
			}
		}
	}

	private void updateDoLoops(JumpExpression exp, int currentStackPosition, ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack) {
		if ((exp.getThenBranch() != null && !exp.getThenBranch().isEmpty()) || (exp.getElseBranch() != null && !exp.getElseBranch().isEmpty())) return;
		for (int stackPosition = 0; stackPosition < currentStackPosition; stackPosition++) {
			if (stack.get(stackPosition).getLabelId() == exp.getJumpDestination()) {
				exp.setLoopType(JumpExpression.LoopType.DO_WHILE);
				exp.setStartFrameLocation(exp.getJumpDestination());
				exp.getThenBranch().addAll(expressionStack.substack(stackPosition, currentStackPosition));
				exp.negate();
				break;
			}
		}
	}

	private void fixLoopDetection(JumpExpression topLevelExp, int loopStartLabel, ExpressionStack.Item stackItem) {
		if (!(stackItem.getExpression() instanceof JumpExpression) || ((JumpExpression) stackItem.getExpression()).getThenBranch() == null) {
			return;
		}
		JumpExpression expression = ((JumpExpression) stackItem.getExpression());
		for (ExpressionStack.Item item : expression.getThenBranch().getAll()) {
			fixLoopDetection(topLevelExp, loopStartLabel, item);
			if (item.getExpression() instanceof JumpExpression && ((JumpExpression) item.getExpression()).getJumpDestination() == loopStartLabel) {
				topLevelExp.setElseBranchEnd(((JumpExpression) item.getExpression()).getJumpDestination());
			}
		}

		if (((JumpExpression) stackItem.getExpression()).getElseBranch() != null) {
			for (ExpressionStack.Item item : expression.getElseBranch().getAll()) {
				fixLoopDetection(topLevelExp, loopStartLabel, item);
				if (item.getExpression() instanceof JumpExpression && ((JumpExpression) item.getExpression()).getJumpDestination() == loopStartLabel) {
					topLevelExp.setElseBranchEnd(((JumpExpression) item.getExpression()).getJumpDestination());
				}
			}
		}
	}

	private void improveBreakAndContinue(JumpExpression exp, int nextLabel, JumpExpression topLevelExpression) {
		List<ExpressionStack.Item> thenBranch;
		List<ExpressionStack.Item> elseBranch;

		if (exp.getThenBranch() != null) {
			thenBranch = exp.getThenBranch().getAll();
			for (ExpressionStack.Item innerExp : thenBranch) {
				if (innerExp.getExpression() instanceof JumpExpression) {
					improveBreakAndContinue((JumpExpression) innerExp.getExpression(), nextLabel, topLevelExpression);
				}
			}
			addBreak(nextLabel, topLevelExpression, thenBranch);
			addContinue(exp, topLevelExpression, thenBranch);
		}

		if (exp.getElseBranch() != null) {
			elseBranch = exp.getElseBranch().getAll();
			for (ExpressionStack.Item innerExp : elseBranch) {
				if (innerExp.getExpression() instanceof JumpExpression) {
					improveBreakAndContinue((JumpExpression) innerExp.getExpression(), nextLabel, topLevelExpression);
				}
			}

			addBreak(nextLabel, topLevelExpression, elseBranch);
			addContinue(exp, topLevelExpression, elseBranch);
		}
	}

	private void addBreak(int nextLabel, JumpExpression topLevelExpression, List<ExpressionStack.Item> branch) {
		if (branch.isEmpty()) return;
		ExpressionStack.Item lastItem = branch.get(branch.size() - 1);
		if (lastItem.getExpression() instanceof UnconditionalJump) {
			ExpressionStack.Item lastElseBranchItem = branch.get(branch.size() - 1);
			if (topLevelExpression.isLoop() &&
					((UnconditionalJump) lastElseBranchItem.getExpression()).getJumpDestination() == nextLabel) {
				lastElseBranchItem.setExpression(new BreakExpression((UnconditionalJump) lastElseBranchItem.getExpression()));
				topLevelExpression.setElseBranchEnd(topLevelExpression.getStartFrameLocation());
			}
		}
	}

	private void addContinue(JumpExpression currentExp, JumpExpression topLevelExp, List<ExpressionStack.Item> branch) {
		if (currentExp.equals(topLevelExp) || branch.isEmpty()) return;

		ExpressionStack.Item topLevelLastItem = topLevelExp.getThenBranch().getAll().get(topLevelExp.getThenBranch().size() - 1);
		ExpressionStack.Item lastItem = branch.get(branch.size() - 1);
		if (lastItem.getExpression() instanceof UnconditionalJump && topLevelLastItem.getExpression() instanceof UnconditionalJump) {
			if (topLevelExp.isLoop() && ((UnconditionalJump) lastItem.getExpression()).getJumpDestination() == topLevelExp.getStartFrameLocation()) {
				lastItem.setExpression(new ContinueExpression((UnconditionalJump) lastItem.getExpression()));
			}
		}
	}
}

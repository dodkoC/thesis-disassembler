package com.thesis.expression.stack;

import com.thesis.expression.*;
import com.thesis.translator.StackEnhancer;
import org.objectweb.asm.Label;

import java.util.*;

public class ExpressionStack {

	private static final int NOT_SET = -1;

	private List<StackEnhancer> mEnhancers;

	private final Stack<StackItem> mStack;
	private int mLineNum;
	private Map<Label, Integer> mLabels;
	private int mLabel;
	private int mLastImprovementPosition = 0;
	private int mVisitedFrame = NOT_SET;
	private Map<Integer, StackItem> mFrameItemMap;

	public ExpressionStack() {
		mLabels = new HashMap<>();
		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
		mEnhancers = new ArrayList<>();
	}

	private ExpressionStack(ExpressionStack original) {
		mLabels = original.mLabels;
		mEnhancers = original.mEnhancers;

		mLineNum = original.mLineNum;
		mLabels = original.mLabels;
		mLabel = original.mLabel;
		mLastImprovementPosition = original.mLastImprovementPosition;

		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
	}

	public ExpressionStack getNew() {
		return new ExpressionStack(this);
	}

	public void addEnhancer(StackEnhancer enhancer) {
		mEnhancers.add(enhancer);
	}

	public void enhance() {
		if(mEnhancers.isEmpty()) {
			return;
		}
		for (StackEnhancer enhancer : mEnhancers) {
			enhancer.enhance(this, mStack);
		}
	}

	public void push(Expression expression) {
		expression.setLine(mLineNum);
		expression.prepareForStack(this); //TODO move prepare to expression
		mStack.push(new StackItem(expression, mLabel, mLineNum));
		improveStack();
		expression.afterPush(this);
		if (mVisitedFrame != NOT_SET) {
			mFrameItemMap.put(mVisitedFrame, mStack.peek());
			mVisitedFrame = NOT_SET;
		}
	}

	public void push(Expression expression, boolean shouldUpdateStack) {
		if (shouldUpdateStack) {
			push(expression);
		} else {
			mStack.push(new StackItem(expression, mLabel, mLineNum));
		}
	}

//	public void pushBelow(Expression expression, int position) {
//		//TODO preparation for DUP instructions
//	}

	private void improveStack() {
		for (int i = mLastImprovementPosition; i < mStack.size(); i++) { //todo think if ok
			Expression currentExp = mStack.get(i).getExpression();
			if (currentExp instanceof UnaryExpression) {
				if(((UnaryExpression) currentExp).isPostfix()) {
					mStack.remove(i-1);
					mLastImprovementPosition = i - 1;
				} else if (i + 1 < mStack.size()) {
					mStack.remove(i+1);
					mLastImprovementPosition = i + 1;
				}
			} else if (currentExp instanceof MonitorExpression && mStack.size() > i+1) {
				Expression followingExp = mStack.get(i+1).getExpression();
				if (followingExp instanceof TryCatchExpression) {
					((MonitorExpression) currentExp).setSynchronizedBlock((TryCatchExpression)followingExp);
					mStack.remove(i+1);
					mLastImprovementPosition = i + 1;
				}
			}
		}
	}

	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().getExpression();
	}

	public Expression pop() {
		if (mStack.isEmpty()) return null;
		return mStack.pop().getExpression();
	}

	public Expression get(int index) {
		return mStack.get(index).getExpression();
	}

	public void swap() {
		StackItem first = mStack.pop();
		StackItem second = mStack.pop();
		mStack.push(first);
		mStack.push(second);
	}

	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(StackItem exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	public List<StackItem> getAll() {
		return Arrays.asList(mStack.toArray(new StackItem[mStack.size()]));
	}

//	public void clear() {
//		mStack.clear();
//	}

	public Expression remove(int index) {
		return mStack.remove(index).getExpression();
	}

	public void setLineNumber(int line) {
		mLineNum = line;
	}

	public void setLabel(int label) {
		mLabel = label;
	}

	public int getLabel() {
		return mLabel;
	}

	public int size() {
		return mStack.size();
	}

	public int getLabelId(final Label l) {
		Integer labelId = mLabels.get(l);
		if (labelId == null) {
			labelId = mLabels.size();
			mLabels.put(l, labelId);
		}
		return labelId;
	}

	public boolean isEmpty() {
		return mStack.isEmpty();
	}

	public ExpressionStack duplicate() {
		ExpressionStack copy = getNew();
		copy.mStack.addAll(this.mStack);
		return copy;
	}

	public void addFrame(int currentLabel) {
		mVisitedFrame = currentLabel;
	}

	public int getExpressionIndexOfFrame(int label) {
		StackItem item = mFrameItemMap.get(label);
		if (item == null) {
			return -1;
		}
		return mStack.indexOf(item);
	}

	public ExpressionStack substack(int startIndex, int endIndex) {
		ExpressionStack subStack = getNew();
		for(int i = 0; i < endIndex - startIndex; i++ ) {
			subStack.mStack.push(mStack.remove(startIndex));
		}
		return subStack;
	}
}
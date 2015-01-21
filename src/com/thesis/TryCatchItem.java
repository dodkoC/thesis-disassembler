package com.thesis;

import com.thesis.expression.ExpressionStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TryCatchItem {

	private int mStartId;
	private int mEndId;
	private List<Integer> mHandlerLocations = new ArrayList<>();
	private Map<Integer, String> mHandlerTypes = new HashMap<>(); // labelId, type
	private ExpressionStack mTryStack;
	private Map<Integer, ExpressionStack> mCatchStacks = new HashMap<>(); //labelId, stack
	private ExpressionStack mFinallyStack;

	public TryCatchItem(int startId, int endId, int handlerId, String exception) {
		mStartId = startId;
		mEndId = endId;
		mHandlerLocations.add(handlerId);
		mHandlerTypes.put(handlerId, exception);
	}

	public int getStartId() {
		return mStartId;
	}

	public int getEndId() {
		return mEndId;
	}

	public ExpressionStack getTryStack() {
		return mTryStack;
	}

	public void setTryStack(ExpressionStack tryStack) {
		mTryStack = tryStack;
	}

	public ExpressionStack getFinallyStack() {
		return mFinallyStack;
	}

	public void setFinallyStack(ExpressionStack finallyStack) {
		mFinallyStack = finallyStack;
	}

	public Map<Integer, String> getHandlerTypes() {
		return mHandlerTypes;
	}

	public List<Integer> getHandlerLocations() {
		return mHandlerLocations;
	}

	public int getHandlerCount() {
		return mHandlerTypes.size();
	}

	public int getCatchBlockCount() {
		return mCatchStacks.size();
	}

	public void addHandlers(List<Integer> handlerLocations, Map<Integer,String> handlers) {
		mHandlerLocations.addAll(handlerLocations);
		mHandlerTypes.putAll(handlers);
	}

	public boolean matches(TryCatchItem other) {
		return this.mStartId == other.mStartId && this.mEndId == other.mEndId;
	}

	public boolean isHandlerLabel(int label) {
		for(int key : mHandlerTypes.keySet()) {
			if (key == label) return true;
		}
		return false;
	}

	public void addCatchBlock(int handlerId, ExpressionStack catchStack) {
		mCatchStacks.put(handlerId, catchStack);
	}

	public ExpressionStack getCatchBlock(int handlerId) {
		return mCatchStacks.get(handlerId);
	}

	public String getHandlerType(int handlerId) {
		return mHandlerTypes.get(handlerId);
	}

}

package com.thesis;

import com.thesis.common.DataType;

public class Variable {

	protected String mName;
	protected DataType mType;
	protected boolean mDebugType;


	public Variable() {
		mDebugType = false;
		mType = DataType.UNKNOWN;
	}

	public Variable(String name, DataType type) {
		mDebugType = false;
		mName = name;
		mType = type != null ? type : DataType.UNKNOWN;
	}

	public DataType getType() {
		return mType;
	}

	public void setType(DataType type) {
		this.mType = type;
	}

	public void setName(String name) {
		mName = name;
	}

	public boolean hasType() {
		return mType != null && !mType.toString().isEmpty() && !mType.equals(DataType.UNKNOWN);
	}

	public boolean hasDebugType(){
		return mDebugType;
	}

}

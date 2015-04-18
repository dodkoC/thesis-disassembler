package com.thesis.expression;

import com.thesis.Writable;
import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.StringWriter;

public abstract class Expression implements Writable {

	protected int mOpCode;
	protected DataType mType;
	protected DataType mCastType;
	protected int mLine;

	public Expression(int opCode) {
		mOpCode = opCode;
	}

	abstract public DataType getType();

	public void setType(DataType type){
		mType = type;
	}

	public void setCastType(DataType type) {
		mCastType = type;
	}

	public int getLine() {
		return mLine;
	}

	public void setLine(int line) {
		mLine = line;
	}

	public void setOpCode(int opCode) {
		mOpCode = opCode;
	}

	public boolean hasType() {
		return mType != null && !mType.toString().isEmpty() && !mType.equals(DataType.UNKNOWN);
	}

	public boolean isVirtual() {
		return false;
	}

	abstract public void prepareForStack(ExpressionStack stack);

	public void afterPush(ExpressionStack stack){

	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			write(writer);
		} catch (IOException e) {
			//todo
			e.printStackTrace();
		}
		return  writer.toString();
	}
}

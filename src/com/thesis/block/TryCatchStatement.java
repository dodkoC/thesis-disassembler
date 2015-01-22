package com.thesis.block;

import com.thesis.StatementCreator;
import com.thesis.expression.TryCatchExpression;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TryCatchStatement extends Statement {

	private BlockStatement mTryBlock;
	private List<CatchStatement> mCatchBlocks;
	private BlockStatement mFinallyBlock;

	public TryCatchStatement(TryCatchExpression tryCatchExpression, int line) {
		super(tryCatchExpression, line);
		mTryBlock = new BlockStatement(line, new StatementCreator(tryCatchExpression.getTryStack()).getStatements());
		if (tryCatchExpression.getFinallyStack() != null && !tryCatchExpression.getFinallyStack().isEmpty()) {
			mFinallyBlock = new BlockStatement(line, new StatementCreator(tryCatchExpression.getFinallyStack()).getStatements());
		}
		mCatchBlocks = new ArrayList<>();
		for(TryCatchExpression.CatchExpression catchExpression : tryCatchExpression.getCatchExpressions()) {
			mCatchBlocks.add(new CatchStatement(catchExpression));
		}
	}

	@Override
	public Block disassemble() {
		return this;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("try");
		mTryBlock.write(writer);
		for (CatchStatement catchBlock : mCatchBlocks) {
			catchBlock.write(writer);
		}
		if (mFinallyBlock != null) {
			writer.write(" finally");
			mFinallyBlock.write(writer);
			writer.write(NL);
		} else {
			writer.write(NL);
		}
	}

	private class CatchStatement extends Statement {

		private BlockStatement mCatchBlock;

		protected CatchStatement(TryCatchExpression.CatchExpression catchExpression) {
			super(catchExpression, catchExpression.getLine());
			mCatchBlock = new BlockStatement(this.mLine, new StatementCreator(catchExpression.getStack()).getStatements());
		}

		@Override
		public void write(Writer writer) throws IOException {
			mExpression.write(writer);
			mCatchBlock.write(writer);
		}
	}
}
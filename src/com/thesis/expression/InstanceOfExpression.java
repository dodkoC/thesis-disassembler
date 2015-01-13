package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.IOException;
import java.io.Writer;

public class InstanceOfExpression extends Expression {

	private Expression leftObject;
	private DataType rightClass;

	public InstanceOfExpression(TypeInsnNode instruction) {
		super(instruction);
		rightClass = DataType.getType(Util.javaObjectName(instruction.desc));
	}

	@Override
	public DataType getType() {
		return DataType.BOOLEAN;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		leftObject = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		leftObject.write(writer);
		writer.append(" instanceof ").append(rightClass.toString());
	}
}

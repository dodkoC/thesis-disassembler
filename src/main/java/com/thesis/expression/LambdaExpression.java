package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Expression that represents the dynamic method invocation
 *<p>
 * used for the INVOKEDYNAMIC instruction
 */
public class LambdaExpression extends Expression{

	private MethodHandle mBsMethod;
	private DataType mOwner;
	private String mName;
	private List<Argument> mArguments = new ArrayList<>();
	private List<DataType> methodArgs= new ArrayList<>();

	public LambdaExpression(String name, String desc, Handle bsm, Object... bsmArgs) {
		super(Opcodes.INVOKEDYNAMIC);

		mOwner = DataType.getTypeFromObject(bsm.getOwner());
		mName = bsm.getName();

		mArguments.add(0, new ArgumentImpl("/*stacked automatically by the VM*/ java.lang.invoke.MethodHandles.lookup()"));
		mArguments.add(1, new StringArgument(name));
		mArguments.add(2, new MethodType(desc));

		appendArgsAtPosition(mArguments, 3, bsmArgs);

		for (Type arg : Type.getMethodType(desc).getArgumentTypes()) {
			methodArgs.add(DataType.getType(arg));
		}
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (methodArgs != null) {
			for (int i = 0; i < methodArgs.size(); i++) {
				stack.pop();
			}
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(mOwner.print()).append(".").append(mName).write("(");
		for (int i = 0; i < mArguments.size(); i++) {
			if (i > 0) {
				writer.write(", ");
			}
			mArguments.get(i).write(writer);
		}
		writer.write(")");
	}

	private static void appendArgsAtPosition(List<Argument> mArguments, int offset, Object[] args) {
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			Argument argument;
			if (arg instanceof Handle) {
				argument = new MethodHandle((Handle)arg);
			} else if (arg instanceof Type) {
				argument = new MethodType(((Type) arg).getDescriptor());
			} else {
				argument = new ArgumentImpl(arg.toString());
			}
			mArguments.add(i + offset, argument);
		}
	}

	private static class MethodHandle implements Argument {

		private static final DataType METHOD_HANDLES = DataType.getTypeFromObject("java.lang.invoke.MethodHandles");
		private static final String LOOKUP = "lookup()";

		private int mTag;
		private List<Argument> mArguments;
		private String mFindMethod;

		public MethodHandle(Handle handle) {
			mTag = handle.getTag();
			mFindMethod = getFindMethod(mTag);
			Type t = Type.getMethodType(handle.getDesc());
			List<Object> args = new ArrayList<>();
			args.add(t.getReturnType());
			Collections.addAll(args, t.getArgumentTypes());

			mArguments = new ArrayList<>();
			mArguments.add(new ClassArgument(DataType.getTypeFromObject(handle.getOwner())));
			mArguments.add(new StringArgument(handle.getName()));
			mArguments.add(new MethodType(args));
		}

		private static String getFindMethod(int tag) {
			switch (tag) {
				case Opcodes.H_GETFIELD:
					return "findGetter";
				case Opcodes.H_GETSTATIC:
					return "findStaticGetter";
				case Opcodes.H_PUTFIELD:
					return "findSetter";
				case Opcodes.H_PUTSTATIC:
					return "findStaticSetter";
				case Opcodes.H_INVOKEVIRTUAL:
					return "findVirtual";
				case Opcodes.H_INVOKESTATIC:
					return "findStatic";
				case Opcodes.H_INVOKESPECIAL:
					return "findSpecial";
				case Opcodes.H_NEWINVOKESPECIAL:
					return "findConstructor";
				case Opcodes.H_INVOKEINTERFACE:
					return "findVirtual";
			}
			return "find";
		}

		public void write(Writer writer) throws IOException{
			writer.append(METHOD_HANDLES.print()).append(".").append(LOOKUP).append(".").write(mFindMethod);
			writer.write("(");
			for(int i = 0; i < mArguments.size(); i++) {
				if (i > 0) {
					writer.write(", ");
				}
				mArguments.get(i).write(writer);
			}
			writer.write(")");
		}
	}

	private static class MethodType implements Argument {

		private static DataType METHOD_TYPE = DataType.getTypeFromObject("java.lang.invoke.MethodType");
		private static String METHOD = "methodType";
		private List<ClassArgument> mTypes = new ArrayList<>();

		public MethodType(List<Object> types) {
			for(Object o : types) {
				addType(mTypes, (Type) o);
			}
		}

		private static void addType(List<ClassArgument> dataTypes, Type type) {
			dataTypes.add(new ClassArgument(DataType.getTypeFromObject(type.getReturnType().getClassName())));
		}

		public MethodType(String desc) {
			Type methodType = Type.getMethodType(desc);
			addType(mTypes, methodType);
		}

		public void write(Writer writer) throws IOException {
			writer.append(METHOD_TYPE.print()).append(".").append(METHOD).write("(");

			for(int i = 0; i < mTypes.size(); i++) {
				if (i > 0) {
					writer.write(", ");
				}
				mTypes.get(i).write(writer);
			}

			writer.write(")");
		}
 	}

	private static class StringArgument implements Argument {

		private String mValue;

		public StringArgument(String value) {
			mValue = value;
		}

		@Override
		public void write(Writer writer) throws IOException {
			writer.append("\"").append(mValue).append("\"");
		}
	}

	private static class ClassArgument implements Argument {

		private DataType mValue;

		public ClassArgument(DataType value) {
			mValue = value;
		}

		@Override
		public void write(Writer writer) throws IOException {
			writer.append(mValue.print()).append(".class");
		}
	}

	private static class ArgumentImpl implements Argument {
		private String mValue;

		public ArgumentImpl(String value) {
			mValue = value;
		}

		@Override
		public void write(Writer writer) throws IOException {
			writer.write(mValue);
		}
	}

	private interface Argument {
		void write(Writer writer) throws IOException;
	}
}

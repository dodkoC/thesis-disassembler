public class MethodInsnNode_invokespecial {

	public MethodInsnNode_invokespecial() {
		super();
	}

	private void createObjects() {
		java.lang.String arg = new java.lang.String();
		MethodInsnNode_invokespecial arg2 = new MethodInsnNode_invokespecial();
		arg2.anotherPrivateMethod(arg);
		anotherPrivateMethod(arg);
	}

	private java.lang.String anotherPrivateMethod(java.lang.String string) {
		return string;
	}

	public void callPrivateMethod(java.lang.String string) {
		anotherPrivateMethod(string);
	}
}

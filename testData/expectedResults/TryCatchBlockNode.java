class TryCatchBlockNode {

	TryCatchBlockNode() {
		super();
	}

	void innerTryCatch() {
		java.lang.String value;
		int number;
		java.lang.RuntimeException e;
		java.lang.Object var4;
		value = "4";
		try {
			try {
				number = java.lang.Integer.valueOf(value).intValue();
				java.lang.System.out.println("no exception");
			} catch (java.lang.NegativeArraySizeException e) {
				number = -6;
				java.lang.System.out.println("NegativeArraySizeException caught");
			}
		} catch (java.lang.NumberFormatException e) {
			number = -1;
			java.lang.System.out.println("number exception caught");
		} catch (java.lang.IndexOutOfBoundsException e) {
			number = -3;
			java.lang.System.out.println("index out of bounds exception caught");
		} catch (java.lang.NullPointerException | java.lang.ArithmeticException e) {
			number = 5;
			java.lang.System.out.println("multicatch block");
		} finally {
			java.lang.System.out.println("called finally");
		}
		java.lang.System.out.println(new java.lang.StringBuilder().append("value is").append(number).toString());
	}

	void onlyFinally() {
		int number;
		java.lang.Object var2;
		number = 0;
		try {
			number = java.lang.Integer.valueOf("3").intValue();
		} finally {
			java.lang.System.out.println(new java.lang.StringBuilder().append("called finally with ").append(number).toString());
		}
	}

	void catchWithThrow() throws java.io.IOException {
		java.lang.String a;
		java.lang.NullPointerException e;
		java.lang.Object var3;
		a = "a";
		try {
			e = a.length();
		} catch (java.lang.NullPointerException e) {
			a = "caught";
			throw new java.io.IOException(e);
		} finally {
			a = "finally";
		}
	}

	void simplePrintFile() throws java.io.IOException {
		java.io.FileInputStream input;
		int data;
		java.lang.Object var3;
		input = new java.io.FileInputStream("file.txt");
		try {
			data = input.read();
			while (data != -1) {
				java.lang.System.out.print((char) data);
				data = input.read();
			}
		} finally {
			if (input == null) {
				java.lang.System.out.println("INPUT IS NULL");
			}
			if (input != null) {
				input.close();
			} else {
				java.lang.System.out.println("ELSE BRANCH");
			}
		}
	}

	void tryWithResourcesLikePrintFile() throws java.io.IOException {
		java.io.FileInputStream input;
		java.lang.Throwable exception;
		int throwable;
		java.lang.Object var4;
		java.lang.Throwable innerException;
		input = new java.io.FileInputStream("file.txt");
		exception = null;
		try {
			throwable = input.read();
			while (throwable != -1) {
				java.lang.System.out.print((char) throwable);
				throwable = input.read();
			}
		} catch (java.lang.Throwable throwable) {
			exception = (java.lang.Throwable) throwable;
			throw throwable;
		} finally {
			if (input != null) {
				if (exception != null) {
					try {
						input.close();
					} catch (java.lang.Throwable innerException) {
						exception.addSuppressed(innerException);
					}
				} else {
					input.close();
				}
			}
		}
	}

	void tryWithResourcesPrintFile() throws java.io.IOException {
		java.io.FileInputStream input;
		java.lang.Object var2;
		int data;
		java.lang.Object var4;
		java.lang.Object var5;
		input = new java.io.FileInputStream("file.txt");
		var2 = null;
		try {
			data = input.read();
			while (data != -1) {
				java.lang.System.out.print((char) data);
				data = input.read();
			}
		} catch (java.lang.Throwable data) {
			var2 = java.lang.Object;
			throw data;
		} finally {
			if (input != null) {
				if (var2 != null) {
					try {
						input.close();
					} catch (java.lang.Throwable var5) {
						var2.addSuppressed(var5);
					}
				} else {
					input.close();
				}
			}
		}
	}
}

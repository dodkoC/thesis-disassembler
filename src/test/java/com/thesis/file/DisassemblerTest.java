package com.thesis.file;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(value = JUnitParamsRunner.class)
public class DisassemblerTest {

	private static String TEST_FOLDER = "testData" + File.separator;
	private static String RESULTS_FOLDER = "testData" + File.separator + "expectedResults" + File.separator;

	private final JavaCompiler mJavaCompiler = ToolProvider.getSystemJavaCompiler();

	@Test
	@Parameters({"AnotherEmptyInterface", "ClassWithNumericExpressions", "EmptyDeprecatedClass",
			"EmptyDeprecatedInterface", "EmptyEnum", "EmptyInterface", "ComplexVariableNames",
			"MultiANewArrayInsnNode", "TryCatchBlockNode", "ClassWithInnerClasses", "ClassWithAnonymousClasses"})
	public void testUngroupedClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	@Test
	@Parameters(method = "getInsnNodeClasses")
	public void testInsnNode(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "InsnNode_"));
	}

	@Test
	@Parameters(method = "getFieldInsnNodeClasses")
	public void testFieldInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getFieldInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "FieldInsnNode_"));
	}

	@Test
	@Parameters(method = "getJumpInsnNodeClasses")
	public void testJumpInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getJumpInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "JumpInsnNode_"));
	}

	@Test
	@Parameters(method = "getTypeInsnNodeClasses")
	public void testTypeInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getTypeInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "TypeInsnNode_"));
	}

	@Test
	@Parameters(method = "getMethodInsnNodeClasses")
	public void testMethodInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getMethodInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "MethodInsnNode_"));
	}

	@Test
	@Parameters(method = "getInvokedynamicInsnNodeClasses")
	public void testInvokedynamicInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getInvokedynamicInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "InvokedynamicInsnNode_"));
	}

	@Test
	@Parameters(method = "getSwitchInsnNodeClasses")
	public void testSwitchInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, Disassembler.createInstance(TEST_FOLDER)));
	}

	public List<Object[]> getSwitchInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains("SwitchInsnNode"));
	}

	@Test
	@Parameters(method = "param1, param2, param3, param4, param5, param6, param7, param8, param9")
	public void testClassesWithDependencies(String name, String... dependencies) {
		String compileString = TEST_FOLDER + name + ".java";
		ArrayList<String> filesNames = new ArrayList<>(Arrays.asList(dependencies));
		filesNames.add(0, compileString);
		boolean success = compileClass(filesNames.toArray(new String[filesNames.size()]));
		if (!success) {
			fail("COMPILATION FAILED");
		}
		assertEquals("Classes do not equal", getJavaClassContent(name), Disassembler.createInstance(TEST_FOLDER).decompileClassFile(name + ".class").getJavaCode());
	}
	public Object param1(){return $($("EmptyClassWithInterfaces", makeDependencyString("EmptyInterface", "AnotherEmptyInterface")));}
	public Object param2(){return $($("EmptyInterfaceAnnotation", makeDependencyString("EmptyEnum")));}
	public Object param3(){return $($("SimpleAnnotation", makeDependencyString("RepeatableAnnotation")));}
	public Object param4(){return $($("RepeatableAnnotation", makeDependencyString("SimpleAnnotation")));}
	public Object param5(){return $($("EmptyClassWithComplexAnnotation", makeDependencyString("EmptyInterface", "ComplexAnnotation", "EmptyInterfaceAnnotation", "EmptyEnum")));}
	public Object param6(){return $($("EmptyClass", makeDependencyString("EmptyInterface", "EmptyInterfaceAnnotation", "EmptyEnum")));}
	public Object param7(){return $($("ClassWithMethods", makeDependencyString("SimpleAnnotation", "RepeatableAnnotation")));}
	public Object param8(){return $($("ClassWithFields", makeDependencyString("SimpleAnnotation", "RepeatableAnnotation")));}
	public Object param9(){return $($("ComplexAnnotation", makeDependencyString("EmptyInterfaceAnnotation", "EmptyEnum")));}

	private String[] makeDependencyString(String... deps) {
		for (int i=0; i < deps.length; i++) {
			deps[i] = TEST_FOLDER + deps[i] + ".java";
		}
		return deps;
	}

	private List<Object[]> getFilteredClasses(final FileFilter filter) {
		File srcFolder = new File(TEST_FOLDER);
		if (!srcFolder.isDirectory()) throw new RuntimeException("Not a folder");

		File[] files = srcFolder.listFiles(filter);

		return Arrays.asList(files).stream()
				.map(f -> f.getName().replace(".java", ""))
				.map((String name) -> new Object[] {name})
				.collect(Collectors.toList());
	}

	private String getJavaClassContent(String fileName) {
		byte[] fileContents = new byte[0];
		try {
			fileContents = Files.readAllBytes(Paths.get(RESULTS_FOLDER + fileName + ".java"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(fileContents);
	}

	private String compileAndParseClass(String name, Disassembler disassembler) {
		boolean success = compileClass(TEST_FOLDER + name + ".java");
		if (!success) {
			System.out.println("COMPILATION ERROR: " + name);
			return null;
		}
		System.out.println("COMPILATION SUCCESS: " + name);
		System.out.println("PARSING: " + name);
		return disassembler.decompileClassFile(name + ".class").getJavaCode();
	}

	private boolean compileClass(String... names) {
		List<String> options = new ArrayList<>();
		options.add("-g");

		StandardJavaFileManager fileManager = mJavaCompiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(names);
		JavaCompiler.CompilationTask task = mJavaCompiler.getTask(null, null, null, options, null, fileObjects);
		Boolean result = task.call();
		try {
			fileManager.close();
		} catch (IOException e) {
			System.out.println("Error closing the file manager");
		}
		return result != null && result;
	}

	private void printLines(String name, InputStream ins) throws IOException{
		String line;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(name + " " + line);
		}
	}
}
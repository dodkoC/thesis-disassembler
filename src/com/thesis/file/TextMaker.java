package com.thesis.file;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;

public class TextMaker extends Textifier {

    private static final String COMMENT = "// ";
    private static final String NEW_LINE = "\n";
    private static final String LEFT_BRACKET_NL = "{\n";
    private static final String RIGHT_BRACKET_NL = "}\n";


    public TextMaker() {
        super(Opcodes.ASM5);
    }

    private int accessFlags;

    //region classes
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        super.visit(version, access, name, signature, superName, interfaces);
        accessFlags = access;
        int major = version & 0xFFFF;
        int minor = version >>> 16;
        boolean isClass = false;
        boolean isEnum = false;

        buf.setLength(0);
//        todo print to log file
//        buf.append(COMMENT).append("class version ").append(major).append(".").append(minor).append(NEW_LINE);

        if (containsFlag(access,Opcodes.ACC_DEPRECATED)) {
            buf.append("@Deprecated").append(NEW_LINE);
        }

        if (signature != null) {
            //TODO handle signature
            System.out.println(signature);
        }

        appendAccess(access & ~Opcodes.ACC_SUPER);
        if (containsFlag(access, Opcodes.ACC_ANNOTATION)) {
            buf.append("@interface ");
            removeFromBuffer("abstract ");
        } else if (containsFlag(access, Opcodes.ACC_INTERFACE)) {
            buf.append("interface "); // interface is implicitly abstract
            removeFromBuffer("abstract ");
        } else if (!containsFlag(access, Opcodes.ACC_ENUM)) {
            buf.append("class ");
            isClass = true;
        } else {
            isEnum = true;
            removeFromBuffer("final ");
        }

        buf.append(name);

        if (!isEnum) { // every enum implicitly extends java.lang.Enum
            appendSuperClass(superName);
        }

        if (isClass || isEnum) {
            appendInterfaces(interfaces);
        }

        appendBlockBeginning();

        text.add(buf.toString());
    }

    @Override
    public void visitSource(String file, String debug) {
//        super.visitSource(file, debug); TODO logging
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public Textifier visitClassAnnotation(String desc, boolean visible) {
//        return super.visitClassAnnotation(desc, visible); TODO
        return this;
    }

    @Override
    public Printer visitClassTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitClassTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitClassAttribute(Attribute attr) {
        super.visitClassAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public Textifier visitField(int access, String name, String desc, String signature, Object value) {
//        return super.visitField(access, name, desc, signature, value);  TODO
        return this;
    }

    @Override
    public Textifier visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        return super.visitMethod(access, name, desc, signature, exceptions); TODO
        return this;
    }

    @Override
    public void visitClassEnd() {
        super.visitClassEnd();
    }
    //endregion

    //region annotations
    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        super.visitEnum(name, desc, value);
    }

    @Override
    public Textifier visitAnnotation(String name, String desc) {
        return super.visitAnnotation(name, desc);
    }

    @Override
    public Textifier visitArray(String name) {
        return super.visitArray(name);
    }

    @Override
    public void visitAnnotationEnd() {
        super.visitAnnotationEnd();
    }
    //endregion

    //region fields
    @Override
    public Textifier visitFieldAnnotation(String desc, boolean visible) {
        return super.visitFieldAnnotation(desc, visible);
    }

    @Override
    public Printer visitFieldTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitFieldTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitFieldAttribute(Attribute attr) {
        super.visitFieldAttribute(attr);
    }

    @Override
    public void visitFieldEnd() {
        super.visitFieldEnd();
    }
    //endregion

    //region methods
    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
    }

    @Override
    public Textifier visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    @Override
    public Textifier visitMethodAnnotation(String desc, boolean visible) {
        return super.visitMethodAnnotation(desc, visible);
    }

    @Override
    public Printer visitMethodTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitMethodTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public Textifier visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public void visitMethodAttribute(Attribute attr) {
        super.visitMethodAttribute(attr);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
//        super.visitInsn(opcode); TODO
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
//        super.visitIntInsn(opcode, operand); TODO
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
//        super.visitVarInsn(opcode, var); TODO
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
//        super.visitTypeInsn(opcode, type); TODO
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//        super.visitFieldInsn(opcode, owner, name, desc);  TODO
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
//        super.visitMethodInsn(opcode, owner, name, desc); TODO
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//        super.visitMethodInsn(opcode, owner, name, desc, itf); TODO
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
//        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs); TODO
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
//        super.visitJumpInsn(opcode, label); TODO
    }

    @Override
    public void visitLabel(Label label) {
//        super.visitLabel(label); TODO
    }

    @Override
    public void visitLdcInsn(Object cst) {
//        super.visitLdcInsn(cst); TODO
    }

    @Override
    public void visitIincInsn(int var, int increment) {
//        super.visitIincInsn(var, increment); TODO
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
//        super.visitTableSwitchInsn(min, max, dflt, labels); TODO
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
//        super.visitLookupSwitchInsn(dflt, keys, labels); TODO
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
//        super.visitMultiANewArrayInsn(desc, dims); TODO
    }

    @Override
    public Printer visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitInsnAnnotation(typeRef, typePath, desc, visible); TODO
        return this;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
//        super.visitTryCatchBlock(start, end, handler, type); TODO
    }

    @Override
    public Printer visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible); TODO
        return this;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
//        super.visitLocalVariable(name, desc, signature, start, end, index); TODO
    }

    @Override
    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
//        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible); TODO
        return this;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
//        super.visitLineNumber(line, start); TODO
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
//        super.visitMaxs(maxStack, maxLocals); TODO
    }

    @Override
    public void visitMethodEnd() {
        super.visitMethodEnd();
    }
    //endregion

    //region common
    @Override
    public Textifier visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public Textifier visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
    }
    //endregion

    //region utils
    private void appendAccess(int access) {
        if (containsFlag(access, Opcodes.ACC_PRIVATE)){
            buf.append("private ");
        }
        if (containsFlag(access, Opcodes.ACC_PUBLIC)){
            buf.append("public ");
        }
        if (containsFlag(access, Opcodes.ACC_PROTECTED)){
            buf.append("protected ");
        }
        if (containsFlag(access, Opcodes.ACC_FINAL)){
            buf.append("final ");
        }
        if (containsFlag(access, Opcodes.ACC_STATIC)){
            buf.append("static ");
        }
        if (containsFlag(access, Opcodes.ACC_SYNCHRONIZED)){
            buf.append("synchronized ");
        }
        if (containsFlag(access, Opcodes.ACC_VOLATILE)){
            buf.append("volatile ");
        }
        if (containsFlag(access, Opcodes.ACC_TRANSIENT)){
            buf.append("transient ");
        }
        if (containsFlag(access, Opcodes.ACC_ABSTRACT)){
            buf.append("abstract ");
        }
        if (containsFlag(access, Opcodes.ACC_STRICT)){
            buf.append("strictfp ");
        }
        if (containsFlag(access, Opcodes.ACC_SYNTHETIC)){
            buf.append("synthetic ");
        }
        if (containsFlag(access, Opcodes.ACC_MANDATED)){
            buf.append("mandated ");
        }
        if (containsFlag(access, Opcodes.ACC_ENUM)){
            buf.append("enum ");
        }
    }

    private static boolean containsFlag(int value, int flag) {
        return (value & flag) != 0;
    }

    private void appendBlockBeginning() {
        buf.append(" ").append(LEFT_BRACKET_NL);
    }

    private void appendInterfaces(String[] interfaces) {
        if (interfaces != null && interfaces.length > 0) {
            buf.append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                buf.append(javaObjectName(interfaces[i]));
                if (i < interfaces.length - 1) {
                    buf.append(", ");
                }
            }
        }
    }

    private void appendSuperClass(String superName) {
        if (superName != null && !superName.equals("java/lang/Object")) {
            buf.append(" extends ").append(javaObjectName(superName)).append(" ");
        }
    }

    private void removeFromBuffer(String str) {
        int abstractLocation = buf.indexOf(str);
        buf.replace(abstractLocation, abstractLocation + str.length(),"");
    }

    private static String javaObjectName(String objectName) {
        return objectName.replaceAll("/", ".");
    }
    //endregion
}

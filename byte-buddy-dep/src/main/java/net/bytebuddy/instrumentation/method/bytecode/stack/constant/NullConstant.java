package net.bytebuddy.instrumentation.method.bytecode.stack.constant;

import net.bytebuddy.instrumentation.Instrumentation;
import net.bytebuddy.instrumentation.method.bytecode.stack.StackManipulation;
import net.bytebuddy.instrumentation.method.bytecode.stack.StackSize;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Represents a stack manipulation to load a {@code null} pointer onto the operand stack.
 */
public enum NullConstant implements StackManipulation {

    /**
     * The singleton instance.
     */
    INSTANCE(StackSize.SINGLE);

    /**
     * The size impact of loading the {@code null} reference onto the operand stack.
     */
    private final Size size;

    /**
     * Creates a null constant.
     *
     * @param size The size of the constant on the operand stack.
     */
    NullConstant(StackSize size) {
        this.size = size.toIncreasingSize();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Instrumentation.Context instrumentationContext) {
        methodVisitor.visitInsn(Opcodes.ACONST_NULL);
        return size;
    }

    @Override
    public String toString() {
        return "NullConstant." + name();
    }
}

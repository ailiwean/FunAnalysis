package com.ailiwean.iplugins;

public class MethodStatic {

    public static org.objectweb.asm.commons.Method long2Long() {
        return new org.objectweb.asm.commons.Method("valueOf", "(J)Ljava/lang/Long;");
    }

    public static org.objectweb.asm.commons.Method longValues() {
        return new org.objectweb.asm.commons.Method("longValue", "()J");
    }

    public static org.objectweb.asm.commons.Method currentTimeMillis() {
        return new org.objectweb.asm.commons.Method("currentTimeMillis", "()J");
    }

    public static org.objectweb.asm.commons.Method emptyInit() {
        return new org.objectweb.asm.commons.Method("<init>", "()V");
    }

    public static org.objectweb.asm.commons.Method stringBuildAppendLong() {
        return new org.objectweb.asm.commons.Method("append", "(J)Ljava/lang/StringBuilder;");
    }

    public static org.objectweb.asm.commons.Method stringBuildAppendString() {
        return new org.objectweb.asm.commons.Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
    }

    public static org.objectweb.asm.commons.Method toObjString() {
        return new org.objectweb.asm.commons.Method("toString", "()Ljava/lang/String;");
    }

    public static org.objectweb.asm.commons.Method logi() {
        return new org.objectweb.asm.commons.Method("i", "(Ljava/lang/String;J)V");
    }

    public static org.objectweb.asm.commons.Method logw() {
        return new org.objectweb.asm.commons.Method("w", "(Ljava/lang/String;J)V");
    }

    public static org.objectweb.asm.commons.Method loge() {
        return new org.objectweb.asm.commons.Method("e", "(Ljava/lang/String;J)V");
    }


}

package com.ailiwean.iplugins;

public class TypeStatic {

    public static org.objectweb.asm.Type systemType() {
        return org.objectweb.asm.Type.getType("Ljava/lang/System;");
    }

    public static org.objectweb.asm.Type longType() {
        return org.objectweb.asm.Type.getType("Ljava/lang/Long;");
    }

    public static org.objectweb.asm.Type stringBuild() {
        return org.objectweb.asm.Type.getType("Ljava/lang/StringBuilder;");
    }

    public static org.objectweb.asm.Type object() {
        return org.objectweb.asm.Type.getType("Ljava/lang/Object;");
    }

    public static org.objectweb.asm.Type string() {
        return org.objectweb.asm.Type.getType("Ljava/lang/String;");
    }

    public static org.objectweb.asm.Type number() {
        return org.objectweb.asm.Type.getType("Ljava/lang/Number;");
    }

    public static org.objectweb.asm.Type log() {
        return org.objectweb.asm.Type.getType("Lcom/ailiwean/annotation/FunLog;");
    }

}

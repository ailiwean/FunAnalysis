package com.ailiwean.iplugins;

import org.gradle.api.Project;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class AnalysisClassVisitor extends ClassVisitor {

    String className = "";

    Project project;
    ConfigExtension configExtension;

    public AnalysisClassVisitor(int api, ClassVisitor cv, Project project, ConfigExtension configExtension) {
        super(api, cv);
        this.project = project;
        this.configExtension = configExtension;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        String[] item = name.split("/");
        className = item[item.length - 1];
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        return new AdviceAdapter(api, methodVisitor, access, name, desc) {

            private int startTimeIndex;

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter();
                if (!isNeedOpe)
                    return;

                enterMethodInfo(className + ":" + name);
                invokeStatic(TypeStatic.systemType(), MethodStatic.currentTimeMillis());
                startTimeIndex = newLocal(Type.LONG_TYPE);
                storeLocal(startTimeIndex, Type.LONG_TYPE);

            }

            boolean isNeedOpe = false;

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                isNeedOpe = desc.contains("Lcom/ailiwean/annotation/Analysis");
                return super.visitAnnotation(desc, visible);
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode);
                if (!isNeedOpe)
                    return;

                invokeStatic(TypeStatic.systemType(), MethodStatic.currentTimeMillis());
                int endTimeIndex = newLocal(Type.LONG_TYPE);
                storeLocal(endTimeIndex, Type.LONG_TYPE);
                visitLdcInsn(configExtension.tag);
                //类名:方法名
                visitLdcInsn(className + ":" + name);
                loadLocal(endTimeIndex, Type.LONG_TYPE);
                loadLocal(startTimeIndex, Type.LONG_TYPE);
                mv.visitInsn(LSUB);
                invokeStatic(TypeStatic.log(), MethodStatic.logi());
                exitMethodInfo();
            }
        };
    }

    private void enterMethodInfo(String info) {
        project.getLogger().warn("start insert:" + info);
    }

    private void exitMethodInfo() {
        project.getLogger().warn("insert ok!!!");
    }

}

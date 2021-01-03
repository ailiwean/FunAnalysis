package com.ailiwean.iplugins;

import org.gradle.api.Project;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/***
 *  耗时监听
 */
public class AnalysisClassVisitor extends ClassVisitor {

    private String className = "";

    private Project project;
    private ConfigExtension configExtension;
    private boolean isClassAnnotation;

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
        return new AnalysisMethodVisitor(api, methodVisitor,
                access, name,
                desc, this,
                configExtension, project);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        isClassAnnotation = descriptor.contains(Constant.useTimeAnnotationDesc);
        return super.visitAnnotation(descriptor, visible);
    }

    public static class AnalysisMethodVisitor extends AdviceAdapter {

        private final AnalysisClassVisitor classVisitor;
        private final String methodName;
        private final ConfigExtension extension;
        private final Project project;

        protected AnalysisMethodVisitor(
                int api,
                MethodVisitor methodVisitor,
                int access,
                String name,
                String descriptor,
                AnalysisClassVisitor classVisitor,
                ConfigExtension extension,
                Project project) {
            super(api, methodVisitor, access, name, descriptor);
            this.classVisitor = classVisitor;
            this.methodName = name;
            this.extension = extension;
            this.project = project;
            isClassAnnotation = classVisitor.isClassAnnotation;
        }

        private int startTimeIndex;
        boolean isClassAnnotation;
        boolean isMethodAnnotation = false;

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            isMethodAnnotation = desc.contains(Constant.useTimeAnnotationDesc);
            return super.visitAnnotation(desc, visible);
        }


        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            if (!(isMethodAnnotation || isClassAnnotation))
                return;

            enterMethodInfo(classVisitor.className + ":" + methodName);
            invokeStatic(TypeStatic.systemType(), MethodStatic.currentTimeMillis());
            startTimeIndex = newLocal(Type.LONG_TYPE);
            storeLocal(startTimeIndex, Type.LONG_TYPE);

        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            if (!(isMethodAnnotation || isClassAnnotation))
                return;

            invokeStatic(TypeStatic.systemType(), MethodStatic.currentTimeMillis());
            int endTimeIndex = newLocal(Type.LONG_TYPE);
            storeLocal(endTimeIndex, Type.LONG_TYPE);
            visitLdcInsn(extension.tag);
            //类名:方法名
            visitLdcInsn(classVisitor.className + ":" + methodName);
            loadLocal(endTimeIndex, Type.LONG_TYPE);
            loadLocal(startTimeIndex, Type.LONG_TYPE);
            mv.visitInsn(LSUB);
            invokeStatic(TypeStatic.log(), MethodStatic.logi());
            exitMethodInfo();
        }

        private void enterMethodInfo(String info) {
            project.getLogger().warn("start insert:" + info);
        }

        private void exitMethodInfo() {
            project.getLogger().warn("insert ok!!!");
        }

    }

}

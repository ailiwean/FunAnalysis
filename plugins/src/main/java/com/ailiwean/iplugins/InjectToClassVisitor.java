package com.ailiwean.iplugins;

import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

import static com.ailiwean.iplugins.Constant.pileInsertAnnotationDesc;

/***
 *  耗时监听
 */
public class InjectToClassVisitor {

    public static Compute getCompute(Project project) {
        return new Compute(project);
    }

    public static Ope getOpe(ClassVisitor classVisitor, Project project, List<PileInsertInfo> pileInsertInfoList) {
        return new Ope(classVisitor, project, pileInsertInfoList);
    }

    /***
     *  注解标记的一些信息， 要向那个jar下的class方法中插入那个class方法
     */
    public static class PileInsertInfo {

        Type fromClass;
        Type toClass;

        Method fromMethod;
        Method toMethod;

        public PileInsertInfo(Type fromClass, Type toClass, Method fromMethod, Method toMethod) {
            this.fromClass = fromClass;
            this.toClass = toClass;
            this.fromMethod = fromMethod;
            this.toMethod = toMethod;
        }

        @Override
        public String toString() {
            return "PileInsertInfo{" +
                    "fromClass=" + fromClass.getDescriptor() +
                    ", toClass=" + toClass.getDescriptor() +
                    ", fromMethod=" + fromMethod.getDescriptor() +
                    ", toMethod=" + toMethod.getDescriptor() +
                    '}';
        }
    }

    public static interface ResultBack {
        void back(PileInsertInfo insertInfo);
    }

    public static class Compute extends ClassNode {

        private Project project;
        private String classNameSign;
        private ResultBack resultBack;

        private Compute(Project project) {
            super(Opcodes.ASM5);
            this.project = project;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            classNameSign = "L" + name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodNode methodNode = new MethodNode(api, access, name, desc, signature, exceptions) {
                @Override
                public void visitEnd() {
                    super.visitEnd();
                    if (visibleAnnotations == null)
                        return;
                    if (resultBack == null)
                        return;
                    for (AnnotationNode visibleAnnotation : visibleAnnotations) {

                        if (!visibleAnnotation.desc.contains(pileInsertAnnotationDesc))
                            continue;
                        if (visibleAnnotation.values == null)
                            continue;

                        String[] values = visibleAnnotation.values.get(1).toString().split(",");
                        PileInsertInfo insertInfo = new PileInsertInfo(
                                Type.getType(classNameSign),
                                Type.getType(values[0]),
                                new Method(name, "()V"),
                                new Method(values[1],
                                        values[2])
                        );
                        resultBack.back(insertInfo);
                    }
                }
            };
            methods.add(methodNode);
            return methodNode;
        }

        public void bindResult(ResultBack resultBack) {
            this.resultBack = resultBack;
        }

    }

    public static class Ope extends ClassVisitor {

        private Project project;
        private List<PileInsertInfo> pileInsertInfoList;

        public Ope(ClassVisitor classVisitor, Project project, List<PileInsertInfo> pileInsertInfoList) {
            super(Opcodes.ASM5, classVisitor);
            this.project = project;
            this.pileInsertInfoList = pileInsertInfoList;
        }


        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            for (PileInsertInfo insertInfo : pileInsertInfoList) {
                if (insertInfo.toClass.getDescriptor().contains(name))
                    project.getLogger().warn("找到符合特征的" + name);
            }
        }
    }

}

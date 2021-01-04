package com.ailiwean.iplugins;

import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
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

        //方法头或方法尾
        int type;

        Type fromClass;
        Type toClass;

        Method fromMethod;
        Method toMethod;

        public PileInsertInfo(int type, Type fromClass, Type toClass, Method fromMethod, Method toMethod) {
            this.type = type;
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
            classNameSign = "L" + name + ";";
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

                        int type = 0;
                        try {
                            type = Integer.parseInt(visibleAnnotation.values.get(3).toString());
                        } catch (Exception ignored) {
                        }
                        if (type != 0 && type != 1)
                            throw new RuntimeException("injectTo type must TOP or Bottom");

                        String[] values = visibleAnnotation.values.get(1).toString().split(",");
                        PileInsertInfo insertInfo = new PileInsertInfo(
                                type,
                                Type.getType(classNameSign),
                                Type.getType(values[0].replaceAll("[^\\w/;$]", "")),
                                new Method(name, "()V"),
                                new Method(values[1].replaceAll("[^A-Za-z_]", ""),
                                        values[2].replaceAll("[^\\w();/$]", ""))
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
        //Class是否匹配
        boolean isMatchClass;
        int matchIndex = -1;

        public Ope(ClassVisitor classVisitor, Project project, List<PileInsertInfo> pileInsertInfoList) {
            super(Opcodes.ASM5, classVisitor);
            this.project = project;
            this.pileInsertInfoList = pileInsertInfoList;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            for (PileInsertInfo insertInfo : pileInsertInfoList) {
                if (insertInfo.toClass.getDescriptor().contains(name)) {
                    isMatchClass = true;
                    matchIndex++;
                    break;
                }
            }
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            PileInsertInfo pileInsertInfo = pileInsertInfoList.get(matchIndex);

            boolean isMatchMethod = pileInsertInfo.toMethod.getName().equals(name) &&
                    pileInsertInfo.toMethod.getDescriptor().equals(descriptor);

            //类与方法必须同时匹配
            if (!isMatchClass || !isMatchMethod)
                return methodVisitor;

            else return new AdviceAdapter(api, methodVisitor, access, name, descriptor) {

                @Override
                protected void onMethodEnter() {
                    super.onMethodEnter();

                    enterMethodInfo("@@@from >>>" + pileInsertInfo.fromClass.getDescriptor() + ":" +
                            pileInsertInfo.fromMethod.getName() + "\n @@@to >>>" +
                            pileInsertInfo.toClass.getDescriptor() + ":" +
                            pileInsertInfo.toMethod.getName());

                    //方法头插入
                    if (pileInsertInfo.type == 0) {
                        newInstance(pileInsertInfo.fromClass);
                        dup();
                        invokeConstructor(pileInsertInfo.fromClass, MethodStatic.emptyInit());
                        invokeVirtual(pileInsertInfo.fromClass, pileInsertInfo.fromMethod);
                        visitInsn(RETURN);
                    }
                }

                @Override
                protected void onMethodExit(int opcode) {

                    //方法尾插入
                    if (pileInsertInfo.type == 1) {
                        newInstance(pileInsertInfo.fromClass);
                        dup();
                        invokeConstructor(pileInsertInfo.fromClass, MethodStatic.emptyInit());
                        invokeVirtual(pileInsertInfo.fromClass, pileInsertInfo.fromMethod);
                    }

                    exitMethodInfo();
                }

                private void enterMethodInfo(String info) {
                    project.getLogger().warn("start insert:" + info);
                }

                private void exitMethodInfo() {
                    project.getLogger().warn("insert ok!!!");
                }

            };
        }

    }

}

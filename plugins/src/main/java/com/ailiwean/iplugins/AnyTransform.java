package com.ailiwean.iplugins;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class AnyTransform extends Transform {

    Project project;
    private ConfigExtension configExtension;

    //jar注入相关信息
    private List<InjectToClassVisitor.PileInsertInfo> pileInsertInfoList;

    public AnyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return project.getName() + Constant.transformName;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        //本地文件操作
        transformInvocation.getInputs().forEach(transformInput -> {
            transformInput.getDirectoryInputs().forEach(directoryInput -> {

                eachFile(directoryInput.getFile(), file -> {

                    if (file.isDirectory())
                        return;

                    if (!file.getAbsolutePath().endsWith(".class"))
                        return;

                    try {
                        exePileInsertUseTime(file);
                        computeAnnotationSign(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                });

                File outDir = transformInvocation.getOutputProvider()
                        .getContentLocation(directoryInput.getName(),
                                directoryInput.getContentTypes(),
                                directoryInput.getScopes(),
                                Format.DIRECTORY);

                try {
                    if (!directoryInput.getFile().isDirectory())
                        FileUtils.copyFile(directoryInput.getFile(), outDir);
                    else FileUtils.copyDirectory(directoryInput.getFile(), outDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        //jar包操作
        transformInvocation.getInputs().forEach(transformInput -> {
            transformInput.getJarInputs().forEach(jarInput -> {

                if (!jarInput.getFile().getAbsolutePath().endsWith(".jar"))
                    return;
                File outDir = transformInvocation.getOutputProvider()
                        .getContentLocation(
                                jarInput.getName(),
                                jarInput.getContentTypes(),
                                jarInput.getScopes(),
                                Format.JAR);

                try {

                    OutputStream out = new FileOutputStream(outDir);
                    JarOutputStream jarOutputStream = new JarOutputStream(out);

                    JarFile jarFile = new JarFile(jarInput.getFile());
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();

                        byte[] bytes = Utils.inputSteam2Byte(jarFile.getInputStream(jarEntry));
                        if (jarEntry.getName().endsWith(".class") &&
                                !jarEntry.getName().contains("$")) {
                            try {
                                bytes = exePileInsertJar(bytes);
                            } catch (Exception ignored) {
                            }
                        }
                        JarEntry newJar = new JarEntry(jarEntry.getName());
                        jarOutputStream.putNextEntry(newJar);
                        jarOutputStream.write(bytes);
                    }
                    jarOutputStream.close();
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

    }

    /***
     * 耗时计算插桩
     * @param file
     */
    private void exePileInsertUseTime(File file) throws IOException {

        if (configExtension == null)
            configExtension = project.getExtensions().getByType(ConfigExtension.class);

        if (configExtension == null || !configExtension.enableUseTime)
            return;

        ClassReader cr = new ClassReader(new FileInputStream(file));
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
        cr.accept(new AnalysisClassVisitor(Opcodes.ASM5, cw, project, configExtension), EXPAND_FRAMES);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(cw.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();

//        if (file.getAbsolutePath().contains("Test")) {
//            File outputFile = new File("C:\\Users\\Ailiwean\\Desktop\\project\\FunAnalysis\\asmfile\\test.class");
//            if (outputFile.exists())
//                outputFile.delete();
//            outputFile.createNewFile();
//            FileUtils.copyFile(file, outputFile);
//        }
    }

    /***
     *  计算本地文件注解标记的将插入三方jar中
     * @param file
     * @throws IOException
     */
    private void computeAnnotationSign(File file) throws IOException {

        if (configExtension == null)
            configExtension = project.getExtensions().getByType(ConfigExtension.class);

        if (configExtension == null || !configExtension.enableJarInject)
            return;

        InjectToClassVisitor.Compute com = InjectToClassVisitor.getCompute(project);
        com.bindResult(insertInfo -> {
            if (pileInsertInfoList == null)
                pileInsertInfoList = new ArrayList<>();
            pileInsertInfoList.add(insertInfo);
        });
        //读取注解标记
        ClassReader cr = new ClassReader(new FileInputStream(file));
        cr.accept(com, EXPAND_FRAMES);
    }

    private byte[] exePileInsertJar(byte[] bytes) throws IOException {

        if (configExtension == null)
            configExtension = project.getExtensions().getByType(ConfigExtension.class);

        if (configExtension == null || !configExtension.enableJarInject)
            return bytes;

        if (pileInsertInfoList == null || pileInsertInfoList.size() == 0) {
            project.getLogger().warn("not find need insert to jar method");
            return bytes;
        }

        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
        cr.accept(InjectToClassVisitor.getOpe(cw, project, pileInsertInfoList), EXPAND_FRAMES);
        return cw.toByteArray();
    }

    public void eachFile(File originFile, Run run) {

        if (originFile == null)
            return;

        if (!originFile.isDirectory()) {
            run.each(originFile);
            return;
        }

        if (originFile.listFiles() == null || originFile.listFiles().length == 0)
            return;

        for (File item : Objects.requireNonNull(originFile.listFiles())) {
            eachFile(item, run);
        }

    }

    interface Run {
        void each(File file);
    }

}

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
import java.util.Objects;
import java.util.Set;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class AnyTransform extends Transform {

    Project project;

    public AnyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return project.getName() + "funAnalysisTask";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
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
                    if (!jarInput.getFile().isDirectory())
                        FileUtils.copyFile(jarInput.getFile(), outDir);
                    else FileUtils.copyDirectory(jarInput.getFile(), outDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            transformInput.getDirectoryInputs().forEach(directoryInput -> {

                eachFile(directoryInput.getFile(), file -> {

                    if (file.isDirectory())
                        return;

                    if (!file.getAbsolutePath().endsWith(".class"))
                        return;

                    project.getLogger().warn("文件输出路径" + file.getAbsolutePath());

                    try {
                        exeInstrumentation(file);
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
    }

    /***
     * 插桩
     * @param file
     */
    private void exeInstrumentation(File file) throws IOException {
        ClassReader cr = new ClassReader(new FileInputStream(file));
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
        cr.accept(new AnalysisClassVisitor(Opcodes.ASM5, cw, project), EXPAND_FRAMES);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(cw.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();

        if (file.getAbsolutePath().contains("Test")) {
            File outputFile = new File("C:\\Users\\Ailiwean\\Desktop\\project\\FunAnalysis\\asmfile\\test.class");
            if (outputFile.exists())
                outputFile.delete();
            outputFile.createNewFile();
            FileUtils.copyFile(file, outputFile);
        }
    }

    interface Run {
        void each(File file);
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
}

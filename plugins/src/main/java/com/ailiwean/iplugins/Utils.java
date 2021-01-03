package com.ailiwean.iplugins;

import com.android.utils.FileUtils;

import org.gradle.api.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    /***
     *  删除缓存的Transform文件，保证Project构建与Transform同步，省得每次都需要手动rebuild
     * @param project1
     */
    public static void deleteCacheFile(Project project1) {
        File cacheFile = new File(project1.getRootDir(),
                project1.getName() + "\\build\\intermediates\\transforms\\" +
                        project1.getName() + Constant.transformName);

        if (cacheFile.exists()) {
            try {
                FileUtils.deletePath(cacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] inputSteam2Byte(InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, bytesRead);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return new byte[0];
    }
}

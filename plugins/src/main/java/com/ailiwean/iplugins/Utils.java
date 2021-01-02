package com.ailiwean.iplugins;

import com.android.utils.FileUtils;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class Utils {

    /***
     *  删除缓存的Transform文件，保证Project构建与Transform同步，省得每次都rebuild
     * @param project1
     */
    public static void deleteCacheFile(Project project1) {
        File cacheFile = new File(project1.getRootDir(),
                project1.getName() + "\\build\\intermediates\\transforms\\" +
                        project1.getName() + Const.transformName);

        if (cacheFile.exists()) {
            try {
                FileUtils.deletePath(cacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

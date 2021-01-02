package com.ailiwean.iplugins;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class IPlugins implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create(Const.configName, ConfigExtension.class);
        AnyTransform anyTransform = new AnyTransform(project);
        boolean hasApp = project.getPlugins().hasPlugin(AppPlugin.class);
        if (hasApp) {
            AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
            appExtension.registerTransform(anyTransform);
        } else {
            LibraryExtension libraryExtension = project.getExtensions().getByType(LibraryExtension.class);
            libraryExtension.registerTransform(anyTransform);
        }
        project.afterEvaluate(Utils::deleteCacheFile);
    }
}
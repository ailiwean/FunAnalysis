package com.ailiwean.iplugins;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class IPlugins implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        boolean hasApp = project.getPlugins().hasPlugin(AppPlugin.class);
        if (hasApp) {
            System.out.println("useAppExtension");
            AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
            if(appExtension==null)
                System.out.println("useAppExtension为null");
            appExtension.registerTransform(new AnyTransform(project));

        } else {
            System.out.println("useLibExtension");
            LibraryExtension libraryExtension = project.getExtensions().getByType(LibraryExtension.class);
            libraryExtension.registerTransform(new AnyTransform(project));
        }

    }
}
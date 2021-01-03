package com.ailiwean.iplugins;

public class ConfigExtension {

    //方法耗时总开关
    public boolean enableUseTime = true;
    //方法耗时tag
    public String tag = Constant.defaultTag;

    //三方jar注入开关
    public boolean enableJarInject = true;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isEnableUseTime() {
        return enableUseTime;
    }

    public void setEnableUseTime(boolean enableUseTime) {
        this.enableUseTime = enableUseTime;
    }

    public boolean isEnableJarInject() {
        return enableJarInject;
    }

    public void setEnableJarInject(boolean enableJarInject) {
        this.enableJarInject = enableJarInject;
    }
}

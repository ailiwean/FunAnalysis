package com.ailiwean.iplugins;

public class ConfigExtension {

    public boolean enable = true;
    public String tag = Const.defaultTag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

package com.ailiwean.annotation;

import android.util.Log;

public class FunLog {

    private static String tag = "funAnalysis";

    public static void setTag(String tag) {
        FunLog.tag = tag;
    }

    public static void e(String info, long time) {
        Log.e(tag, info + "----------" + time + "ms");
    }

    public static void w(String info, long time) {
        Log.w(tag, info + "----------" + time + "ms");
    }

    public static void i(String info, long time) {
        Log.i(tag, info + "----------" + time + "ms");
    }

}
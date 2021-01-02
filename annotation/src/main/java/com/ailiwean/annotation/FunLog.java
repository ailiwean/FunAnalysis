package com.ailiwean.annotation;

import android.util.Log;

public class FunLog {

    public static void e(String tag, String info, long time) {
        Log.e(tag, info + "----------" + time + "ms");
    }

    public static void w(String tag, String info, long time) {
        Log.w(tag, info + "----------" + time + "ms");
    }

    public static void i(String tag, String info, long time) {
        Log.i(tag, info + "----------" + time + "ms");
    }

}
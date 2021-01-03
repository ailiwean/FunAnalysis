package com.ailiwean.annotation;

import android.util.Log;

public class FunLog {

    static Execute execute;

    public static void regSelfExecute(Execute execute) {
        FunLog.execute = execute;
    }

    public static void e(String tag, String info, long time) {
        Log.e(tag, info + "----------" + time + "ms");
        if (execute != null)
            execute.e(tag, info, time);
    }

    public static void w(String tag, String info, long time) {
        Log.w(tag, info + "----------" + time + "ms");
        if (execute != null)
            execute.w(tag, info, time);
    }

    public static void i(String tag, String info, long time) {
        Log.i(tag, info + "----------" + time + "ms");
        if (execute != null)
            execute.i(tag, info, time);
    }

    public interface Execute {

        void i(String tag, String info, long time);

        void w(String tag, String info, long time);

        void e(String tag, String info, long time);

    }

}
package com.ailiwean.funanalysis;

import android.widget.Toast;

import com.ailiwean.annotation.InjectTo;

public class Test {

    @InjectTo(targetMethod = {"Landroidx/appcompat/app/AppCompatActivity;", "onCreate", "(Landroid/os/Bundle;)V"},
            type = InjectTo.BOTTOM)
    public void test() {
        App app = App.getApp();
        if (app != null)
            Toast.makeText(app.getTopActivity(), "test", Toast.LENGTH_LONG).show();
    }

}

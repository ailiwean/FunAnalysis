package com.ailiwean.funanalysis;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ailiwean.annotation.InjectTo;


public class MainActivity extends AppCompatActivity {

    @InjectTo(targetMethod = {"Landroidx/appcompat/app/AppCompatActivity", "onCreate", "(Landroid/os/Bundle;)V"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}

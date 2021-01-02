package com.ailiwean.funanalysis;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ailiwean.annotation.Analysis;

public class MainActivity extends AppCompatActivity {

    @Analysis
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long var0 = System.currentTimeMillis();
        long var1 = System.currentTimeMillis();

    }
}

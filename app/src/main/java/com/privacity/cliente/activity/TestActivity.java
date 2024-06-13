package com.privacity.cliente.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void doit(View v){
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public  void call() throws IOException {

        new RestTemplateTest(this).execute();
        }


    }



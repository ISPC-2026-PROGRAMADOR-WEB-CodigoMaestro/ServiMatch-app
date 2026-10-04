package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("LOGIN_DEBUG", "ENTRÉ A DASHBOARD");

        setContentView(R.layout.activity_dashboard);
    }
}
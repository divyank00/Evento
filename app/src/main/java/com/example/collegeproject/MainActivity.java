package com.example.collegeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.collegeproject.auth.AuthActivity;
import com.example.collegeproject.base.BaseActivity;

import static com.example.collegeproject.helper.SharedPref.getToken;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        finish();
        if(getToken(this)!=null){
            startActivity(new Intent(this, BaseActivity.class));
        }else
            startActivity(new Intent(this, AuthActivity.class));
    }
}
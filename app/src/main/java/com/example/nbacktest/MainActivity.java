package com.example.nbacktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLevelTest(View v){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onClickPractice(View v){
        Intent intent = new Intent(this, PracticeGameSettingActivity.class);
        startActivity(intent);
    }

}
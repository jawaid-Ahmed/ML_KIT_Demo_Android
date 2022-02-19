package com.auzware.mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {


    Button mainButton,cameraXBtn,extraBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mainButton=findViewById(R.id.mainActivityButn);
        cameraXBtn=findViewById(R.id.cameraXActivityBUtn);
        extraBtn=findViewById(R.id.extraBtn);


        mainButton.setOnClickListener(this);
        cameraXBtn.setOnClickListener(this);
        extraBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mainActivityButn:
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                break;
            case R.id.cameraXActivityBUtn:
                startActivity(new Intent(StartActivity.this,CameraXActivity.class));
                break;
            case R.id.extraBtn:
                startActivity(new Intent(StartActivity.this,AccessStorageActivity.class));
                break;
        }
    }
}
package com.zqtech.dataapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.jump_basement_btn:
                break;
            case R.id.jump_elevator_btn:
                break;
            case R.id.jump_energy_btn:
                break;
            case R.id.jump_face_btn:
                intent = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(intent);
                break;
            case R.id.jump_vehicle_btn:
                intent = new Intent(MainActivity.this, VehicleActivity.class);
                startActivity(intent);
                break;
        }
    }
}

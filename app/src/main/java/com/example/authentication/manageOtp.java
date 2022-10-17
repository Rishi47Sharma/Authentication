package com.example.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class manageOtp extends AppCompatActivity {
   EditText t2;
   Button b2;
   String phoneNumber;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);
        t2=(EditText)findViewById(R.id.enterOtp);
        b2=(Button)findViewById(R.id.verifyOtp);
        phoneNumber=getIntent().getStringExtra("mobile").toString();
        initiateOtp();

    }

    private void initiateOtp() {

    }
}
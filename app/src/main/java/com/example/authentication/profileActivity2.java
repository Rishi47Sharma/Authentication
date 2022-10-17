package com.example.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.authentication.databinding.ActivityMainBinding;
import com.example.authentication.databinding.ActivityProfile2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profileActivity2 extends AppCompatActivity {
    private ActivityProfile2Binding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityProfile2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=firebaseAuth.getInstance();
        checkUserStatus();
        binding.logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();

            }
        });
    }

    private void checkUserStatus() {
       FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
       if(firebaseUser!=null){
           Toast.makeText(profileActivity2.this,"logged in",Toast.LENGTH_SHORT).show();

       }else{
           //user is not logged in
           finish();
       }

    }
}
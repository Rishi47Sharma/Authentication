package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.authentication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //view Binding
   private ActivityMainBinding binding;

   //if code send failed ,will used to resend code OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId; // will hold OtpVerification code
    public static final String TAG ="MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.phoneLl.setVisibility(View.VISIBLE);//show phone layout
        binding.codeLl.setVisibility(View.GONE);//hide otp layout
        firebaseAuth=firebaseAuth.getInstance();
        binding.ccp.registerCarrierNumberEditText(binding.phoneEt);

        //initiate progress
     pd = new ProgressDialog(this);
     pd.setTitle("Please wait.....");
     pd.setCanceledOnTouchOutside(false);
     mCallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
         @Override
         public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //this callback will be invoked in two situation
             signInWithPhoneAuthCredential(phoneAuthCredential);
         }

         @Override
         public void onVerificationFailed(@NonNull FirebaseException e) {
             pd.dismiss();
             Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

         }

         @Override
         public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
             super.onCodeSent(verificationId, forceResendingToken);
             // The sms verification code has been sent to the provided phone number ,
             // now need to ask the user to enter the code and then construct a credential
             //by combining the code with verification Id.
             String phone = binding.ccp.getFullNumberWithPlus().trim();
             Log.d(TAG, "onCodeSent: "+verificationId);
             mVerificationId=verificationId;
             forceResendingToken=token;
             pd.dismiss();
             binding.phoneLl.setVisibility(View.GONE);
             binding.codeLl.setVisibility(View.VISIBLE);




         }

     };
     binding.phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String phone = binding.ccp.getFullNumberWithPlus().trim();
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(MainActivity.this,"Please enter phone number",Toast.LENGTH_SHORT).show();
            }
            else{
                startPhoneNumberVerification(phone);

            }
         }
     });
     binding.resendCodeTv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String phone = binding.ccp.getFullNumberWithPlus().trim();
             if(TextUtils.isEmpty(phone)){
                 Toast.makeText(MainActivity.this,"Please enter phone number",Toast.LENGTH_SHORT).show();
             }
             else{

                 resendVerificationCode(phone,forceResendingToken);

             }

         }
     });
     binding.codeSubmitBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String code =binding.codeEt.getText().toString().trim();
             if(TextUtils.isEmpty(code)){
                 Toast.makeText(MainActivity.this,"Please enter verification code....",Toast.LENGTH_SHORT).show();

             }
             else{
                 verifyPhoneNumberWithCode(mVerificationId,code);
             }

         }
     });


    }
    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying Phone Number");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void resendVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending OTP");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyPhoneNumberWithCode(String VerificationId, String code) {
        pd.setMessage("verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("logging In");
        pd.show();
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //successfully  signed in
                pd.dismiss();
                String Phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                Toast.makeText(MainActivity.this,"logged in as"+Phone,Toast.LENGTH_SHORT).show();
                //start profile activity
                startActivity(new Intent(MainActivity.this,profileActivity2.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }
}
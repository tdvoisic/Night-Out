package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;

public class RegisterActivity extends AppCompatActivity {
    private EditText EmailInput, PasswordInput, ConfirmPasswordInput;
    private Button RegisterAccountButton;
    private FirebaseAuth UserAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EmailInput = (EditText) findViewById(R.id.register_email);
        PasswordInput = (EditText) findViewById(R.id.register_password);
        ConfirmPasswordInput = (EditText) findViewById(R.id.register_confirm_password);
        RegisterAccountButton = (Button) findViewById(R.id.register_button);

        UserAuth = FirebaseAuth.getInstance();

        RegisterAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = UserAuth.getCurrentUser();
        if(currentUser != null){
            SendUserToMainActivity();
        }


    }

    public void CreateAccount(){
        String emailField = EmailInput.getText().toString();
        String passwordField = PasswordInput.getText().toString();
        String confirmPassword = ConfirmPasswordInput.getText().toString();

        if(TextUtils.isEmpty(emailField) || TextUtils.isEmpty(passwordField) || TextUtils.isEmpty(confirmPassword)){
            StyleableToast.makeText(this, "Please fill in all fields to create account", Toast.LENGTH_LONG, R.style.custom_toast_bad).show();

        }else if(!passwordField.equals(confirmPassword)){
            StyleableToast.makeText(this, "Your passwords do not match", Toast.LENGTH_LONG, R.style.custom_toast_bad).show();


        }else{
            UserAuth.createUserWithEmailAndPassword(emailField, passwordField).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToSetUp();
                        StyleableToast.makeText(RegisterActivity.this, "You have been successfully authenticated", Toast.LENGTH_LONG, R.style.custom_toast_good).show();

                    }else{
                        StyleableToast.makeText(RegisterActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG, R.style.custom_toast_bad).show();
                    }
                }
            });

        }


    }

    public void SendUserToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void SendUserToSetUp(){
        Intent setUpIntent = new Intent(RegisterActivity.this, SetUpAccountActivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUpIntent);
        finish();
    }




}
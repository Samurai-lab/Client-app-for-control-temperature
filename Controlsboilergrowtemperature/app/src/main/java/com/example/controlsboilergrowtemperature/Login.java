package com.example.controlsboilergrowtemperature;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private TextView intentTextView, resetTextView;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private <T> void goToIntent(Class<T> newClass) {
        Intent intent = new Intent(getApplicationContext(), newClass);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        loginBtn = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        intentTextView = findViewById(R.id.registerNow);
        resetTextView = findViewById(R.id.resetNow);

        intentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToIntent(Register.class);
            }
        });

        resetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToIntent(ResetPassword.class);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String getEmail, getPassword;

                getEmail = String.valueOf(emailEditText.getText());
                getPassword = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(getEmail)) {
                    Toast.makeText(Login.this, R.string.login_enter_email, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(Login.this, R.string.login_enter_password, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, R.string.successful_login, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, R.string.failed_login,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
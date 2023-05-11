package com.example.controlsboilergrowtemperature;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button resetBtn;
    private TextView backToLogin;
    ProgressBar progressBar;

    private <T> void backToLogin(Class<T> newClass) {
        Intent intent = new Intent(getApplicationContext(), newClass);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetBtn = findViewById(R.id.resetBtn);
        progressBar = findViewById(R.id.progressBar);
        backToLogin = findViewById(R.id.backToLogin);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(emailEditText.getText()).toString();
                if (!email.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ResetPassword.this, R.string.sent_email_reset, Toast.LENGTH_SHORT).show();
                                        backToLogin(Login.class);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ResetPassword.this, R.string.unsuccessful_send_email_reset, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ResetPassword.this, R.string.repeat_write_email_reset, Toast.LENGTH_SHORT).show();
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToLogin(Login.class);
            }
        });
    }
}
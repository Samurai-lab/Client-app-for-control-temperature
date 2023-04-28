package com.example.controlsboilergrowtemperature;

import static android.content.ContentValues.TAG;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private TextView intentTextView;
    ProgressBar progressBar;

    private void backToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            backToLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        registerBtn = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        intentTextView = findViewById(R.id.loginNow);

        intentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String getEmail, getPassword;

                getEmail = String.valueOf(emailEditText.getText());
                getPassword = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(getEmail)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(Register.this, "Entre password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Methods methods = postRetrofitInstance.create(Methods.class);
                                    JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("Registration ", Objects.requireNonNull(emailEditText.getText()).toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                        Call<ResponseBody> call = methods.postData(requestBody);
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    Log.e(TAG, "onFailure: emails :" + response.code());
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                                Log.e(TAG, "onFailure: emails :" + t.getMessage());// Обработка ошибки
                                            }
                                        });
                                    Toast.makeText(Register.this, "Account created",
                                            Toast.LENGTH_SHORT).show();
                                    backToLogin();
                                } else {
                                    Toast.makeText(Register.this, "Authentication failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
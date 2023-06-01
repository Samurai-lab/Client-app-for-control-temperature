package com.example.controlsboilergrowtemperature;

import static android.content.ContentValues.TAG;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.controlsboilergrowtemperature.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private ActivityRegisterBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String regId;
    private String getDataDisplayHelper;

    private void backToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void createUserInDatabase() {
        database = FirebaseDatabase.getInstance(
                "https://cbjt-b7594-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference();

        String getName = binding.nameEditText.getText().toString();
        String getEmail = binding.emailEditText.getText().toString();
        String getPassword = binding.passwordEditText.getText().toString();
        String getAddress = binding.homeAddress.getText().toString();


        HelperClass helperClass = new HelperClass(
                getName,
                getEmail,
                getPassword,
                getAddress);

        int index = Math.min(getEmail.indexOf("@"), getEmail.indexOf("."));
        if (index != -1) {
            String result = getEmail.substring(0, index);

            reference.child("users").child(result)
                    .setValue(helperClass);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            backToLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();

        binding.loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                String getEmail, getPassword;

                getEmail = String.valueOf(binding.emailEditText.getText());
                getPassword = String.valueOf(binding.passwordEditText.getText());

                if (TextUtils.isEmpty(getEmail)) {
                    Toast.makeText(Register.this, R.string.login_enter_email, Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(Register.this, R.string.login_enter_password, Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }

                Methods methods = postRetrofitInstance.create(Methods.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Registration ", Objects.requireNonNull(binding.emailEditText.getText()).toString());
                    jsonObject.put("Address ", Objects.requireNonNull(binding.homeAddress.getText()).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<ResponseBody> callReq = methods.postData(requestBody);
                callReq.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e(TAG, "onResponse: emails :" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: emails :" + t.getMessage());// Обработка ошибки
                    }
                });

                String jsonFileName = "answer.json";
                AnswerAPI answerAPI = RetrofitClient.getRetrofitInstance().create(AnswerAPI.class);
                Call<AnswerModel> call = answerAPI.getUser(jsonFileName);
                call.enqueue(new Callback<AnswerModel>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<AnswerModel> call, Response<AnswerModel> response) {
                        Log.e(TAG, "onResponse: code answer:" + response.code());
                        if (response.code() != 404) {
                            AnswerModel model = response.body();
                            getDataDisplayHelper = model.getAddressAnswer();
                            System.out.println(getDataDisplayHelper);
                        }
                    }

                    @Override
                    public void onFailure(Call<AnswerModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: answer :" + t.getMessage());
                    }
                });


                System.out.println(getDataDisplayHelper);

                if (Objects.equals(getDataDisplayHelper, "true")) {
                    mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    binding.progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, R.string.successful_login,
                                                Toast.LENGTH_SHORT).show();

                                        createUserInDatabase();

                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        backToLogin();
                                    } else {
                                        Toast.makeText(Register.this, R.string.failed_login,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, R.string.address_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
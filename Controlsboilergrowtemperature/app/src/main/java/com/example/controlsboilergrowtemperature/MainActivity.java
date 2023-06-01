package com.example.controlsboilergrowtemperature;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final static String displayVariableKey = "INFO_VARIABLE";

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference reference;
    private ImageButton logout;
    private TextView getDataDisplay, priseTextView;
    private Button postDataBtn;

    private String getDataDisplayHelper;
    private String getDataDisplayHelperNull;
    private String editTextString;

    private TextInputEditText editTextTemperature;

    private void logoutIntent() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private String regId(FirebaseUser user) {
        String result = "";
        int index = Math.min(user.getEmail().indexOf("@"), user.getEmail().indexOf("."));
        if (index != -1) {
            result = user.getEmail().substring(0, index);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTemperature = findViewById(R.id.editTextTemperature);
        logout = findViewById(R.id.logout);
        getDataDisplay = findViewById(R.id.getDataDisplay);
        priseTextView = findViewById(R.id.priseTextView);
        postDataBtn = findViewById(R.id.postDataBtn);

        if (savedInstanceState != null) {
            getDataDisplay.setText(savedInstanceState.getString(displayVariableKey));
            Log.e(TAG, "savedInstanceState:" + savedInstanceState.getString(displayVariableKey));
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            logoutIntent();
        }

        reference = FirebaseDatabase.getInstance(
                        "https://cbjt-b7594-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");
        Query checkAddressDatabase = reference.orderByChild("email").equalTo(user.getEmail());
        checkAddressDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result = snapshot.child(regId(user)).child("address").getValue(String.class);

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        String jsonFileName = result + ".json";
                        Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                        Call<Model> call = methods.getUser(jsonFileName);
                        call.enqueue(new Callback<Model>() {
                            @SuppressLint({"SetTextI18n", "DefaultLocale"})
                            @Override
                            public void onResponse(Call<Model> call, Response<Model> response) {
                                Log.e(TAG, "onResponse: code :" + response.code());
                                if (response.code() != 404) {
                                    Model model = response.body();
                                    getDataDisplay.setText(getDataDisplayHelper);
                                    if (model.getRasxGaza() != null) {
                                        getDataDisplayHelper = "";
                                        getDataDisplayHelper += getString(R.string.hot_water) +
                                                model.getTempGor() + "\n";
                                        getDataDisplayHelper += getString(R.string.cold_water) +
                                                model.getTempXol() + "\n";
                                        getDataDisplayHelper += getString(R.string.air_temperature) +
                                                model.getTempKomn() + "\n";
                                        getDataDisplayHelper += getString(R.string.heater_power) +
                                                model.getPNagr() + "\n";
                                        getDataDisplayHelper += getString(R.string.pomp_work) +
                                                model.getPomp() + "\n";
                                        getDataDisplayHelper += getString(R.string.errors) +
                                                model.getError() + "\n";
                                        getDataDisplayHelper += getString(R.string.gas_consumption) +
                                                model.getRasxGaza() + "\n";
                                        getDataDisplayHelper += getString(R.string.air_consumption) +
                                                model.getRasxVozd() + "\n";
                                        getDataDisplayHelper += getString(R.string.water_pressure) +
                                                model.getDavlVod() + "\n";
                                        getDataDisplayHelper += getString(R.string.gas_pressure) +
                                                model.getDavlGaza() + "\n";
                                        try {
                                            double gasPrice = (double)
                                                    (Double.parseDouble(model.getRasxGaza()) * 19.540);
                                            priseTextView.setText(String.format("%.2f", gasPrice) + " rub");
                                        } catch (IOError e) {
                                            priseTextView.setText("Error value");
                                        }
                                        getDataDisplayHelperNull = getDataDisplayHelper;
                                    } else {
                                        getDataDisplay.setText(getDataDisplayHelperNull);
                                    }
                                } else {
                                    timer.cancel();
                                    Toast.makeText(MainActivity.this,
                                            R.string.unavailable_user,
                                            Toast.LENGTH_SHORT).show();
                                    logoutIntent();
                                }
                            }

                            @Override
                            public void onFailure(Call<Model> call, Throwable t) {
                                Log.e(TAG, "onFailure: emails :" + t.getMessage());
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Disconnect with database", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutIntent();
            }
        });

        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Methods methods = postRetrofitInstance.create(Methods.class);
                JSONObject jsonObject = new JSONObject();
                if (editTextTemperature.getText().toString().length() < 3) {
                    editTextString = editTextTemperature.getText().toString();
                } else {
                    Toast.makeText(MainActivity.this, R.string.warning_temperature_text, Toast.LENGTH_SHORT).show();
                    editTextString = "60";
                }

                if (editTextString.matches("[-+]?\\d+")) {
                    try {
                        jsonObject.put(Objects.requireNonNull(user.getEmail()), editTextString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    Call<ResponseBody> call = methods.postData(requestBody);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: emails :" + response.code());
                                Toast.makeText(MainActivity.this, R.string.temperature_send, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: emails :" + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, R.string.write_number_only, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(displayVariableKey, getDataDisplayHelper);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getDataDisplay.setText(savedInstanceState.getString(displayVariableKey));
    }
}
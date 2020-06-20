package com.has.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.has.MainActivity;
import com.has.R;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.User;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button signIn = findViewById(R.id.button_signIn);

        getApplicationContext().deleteDatabase("HomeAutomation.db"); //TODO za testiranje
        dbManager = new DatabaseManager(getApplicationContext());

        User user = new User(1L, "da@da", "da", "da", "da", null,
                                    System.currentTimeMillis());

        signIn.setOnClickListener(new View.OnClickListener() {
            EditText email = findViewById(R.id.text_enterEmail);
            EditText pass = findViewById(R.id.text_enterPassword);
            @Override
            public void onClick(View v) {
                GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
                //TODO try catch da bude ubudce da vidis dal ima konekcije ako nema da vratis gresku da ne pukne app
                apiService.login(email.getText().toString(),pass.getText().toString()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 200) {
                            Log.d("Connection to server", "200");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putLong("currentUser", response.body().getId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());
                            editor.putString("user", json);
                            editor.apply();
                            startActivity(intent);
                        } else {
                            Log.d("Login credentials", "failed");
                            Toast.makeText(LoginActivity.this, R.string.login_unsuccessful, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
            }
        });

        Button register = findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}

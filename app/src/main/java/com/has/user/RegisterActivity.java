package com.has.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.has.MainActivity;
import com.has.R;
import com.has.data.GetData;
import com.has.data.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button register = findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            EditText email = findViewById(R.id.text_reg_email);
            EditText pass = findViewById(R.id.text_reg_password);
            EditText firstName = findViewById(R.id.text_reg_first_name);
            EditText lastName = findViewById(R.id.text_reg_last_name);
            @Override
            public void onClick(View v) {
                GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
                //TODO try catch da bude ubudce da vidis dal ima konekcije ako nema da vratis gresku da ne pukne app
                apiService.register(email.getText().toString(),pass.getText().toString(),firstName.getText().toString(),lastName.getText().toString()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200) {
                            Log.d("Connection to server", "200");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                        else
                            Log.d("Connection to server", "400");

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
            }
        });
    }
}

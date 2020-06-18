package com.has.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.has.BaseDrawerActivity;
import com.has.R;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.User;
import com.has.user.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_user_profile, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity3);

        Button change = findViewById(R.id.button_user_profile);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newEmailText = findViewById(R.id.text_user_profile_email);
                EditText newPasswordText = findViewById(R.id.text_user_profile_password);
                EditText oldPasswordText = findViewById(R.id.text_user_profile_password_old);
                SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
                Gson gson = new Gson();
                String userJson = sharedPreferences.getString("user", "");
                User user = gson.fromJson(userJson, User.class);
                String newEmail = newEmailText.getText().toString();
                String newPassword = newPasswordText.getText().toString();
                String oldPassword = oldPasswordText.getText().toString();
                if(user.getPassword().equals(oldPassword)) {
                    if(newEmail.length() != 0 || newPassword.length() != 0) {
                        if(!newEmailText.getText().toString().isEmpty()) {
                            user.setEmail(newEmail);
                        } else if(!newPasswordText.getText().toString().isEmpty()) {
                            user.setPassword(newPassword);
                        }
                        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
                        apiService.updateUser(user.getId(), user.getEmail(), user.getPassword()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                    finish();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in email or password fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class UserProfileFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.user_profile_preferences);
            //setPreferencesFromResource(R.xml.user_profile_preferences, rootKey);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentUser", 0);
            Gson gson = new Gson();
            String userJson = sharedPreferences.getString("user", "");
            User user = gson.fromJson(userJson, User.class);
            EditTextPreference emailTextPreference = findPreference("email");
            EditTextPreference firstNameTextPreference = findPreference("firstName");
            EditTextPreference lastNameTextPreference = findPreference("lastName");
            emailTextPreference.setText(user.getEmail());
            firstNameTextPreference.setText(user.getFirstName());
            lastNameTextPreference.setText(user.getLastName());
            /*PreferenceManager.getDefaultSharedPreferences(UserProfileActivity.this)
                    .registerOnSharedPreferenceChangeListener(listener);
*/

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

    private static void bindSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    public static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof EditTextPreference) {
                preference.setSummary(newValue.toString());
                preference.getExtras().getChar("email");
                preference.getExtras().getChar("firstName");
            }
            return false;
        }
    };
}
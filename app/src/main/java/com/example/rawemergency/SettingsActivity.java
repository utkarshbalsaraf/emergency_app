package com.example.rawemergency;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber1;
    private EditText editTextPhoneNumber2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextPhoneNumber1 = findViewById(R.id.editTextPhoneNumber1);
        editTextPhoneNumber2 = findViewById(R.id.editTextPhoneNumber2);
        Button buttonSaveNumbers = findViewById(R.id.buttonSaveNumbers);

        sharedPreferences = getSharedPreferences("MyPhoneNumbers", Context.MODE_PRIVATE);

        buttonSaveNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoneNumbers();
            }
        });

        // Load saved phone numbers (if any) for editing
        editTextPhoneNumber1.setText(sharedPreferences.getString("phoneNumber1", ""));
        editTextPhoneNumber2.setText(sharedPreferences.getString("phoneNumber2", ""));
    }

    private void savePhoneNumbers() {
        String phoneNumber1 = editTextPhoneNumber1.getText().toString().trim();
        String phoneNumber2 = editTextPhoneNumber2.getText().toString().trim();

        if (!phoneNumber1.isEmpty() && !phoneNumber2.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber1", phoneNumber1);
            editor.putString("phoneNumber2", phoneNumber2);
            editor.apply();

            // Notify the user that phone numbers are saved
            Toast.makeText(SettingsActivity.this, "Phone numbers saved", Toast.LENGTH_SHORT).show();

        } else {
            // Show a message indicating that phone numbers cannot be empty
            Toast.makeText(SettingsActivity.this, "Please enter phone numbers", Toast.LENGTH_SHORT).show();
        }
    }
}


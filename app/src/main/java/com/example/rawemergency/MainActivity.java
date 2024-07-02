package com.example.rawemergency;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int SEND_SMS_COUNT = 3;
    private static final int SMS_INTERVAL = 60000; // 1 minute in milliseconds
    private int smsCount = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonOpenSettings = findViewById(R.id.buttonOpenSettings);
        Button buttonSendEmergency = findViewById(R.id.buttonSendEmergency);

        sharedPreferences = getSharedPreferences("MyPhoneNumbers", Context.MODE_PRIVATE);

        buttonOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        buttonSendEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmergencySMS();
            }
        });

        // Request location permission if not granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);




























        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void sendEmergencySMS() {
        String phoneNumber1 = sharedPreferences.getString("phoneNumber1", "");
        String phoneNumber2 = sharedPreferences.getString("phoneNumber2", "");

        if (!phoneNumber1.isEmpty() && !phoneNumber2.isEmpty()) {
            smsCount = 0; // Reset SMS count

            // Start the timer to send SMS at intervals
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (smsCount < SEND_SMS_COUNT) {
                        sendSingleEmergencySMS(phoneNumber1);
                        sendSingleEmergencySMS(phoneNumber2);
                        smsCount++;
                    } else {
                        timer.cancel(); // Stop the timer after sending desired SMS count
                    }
                }
            }, 0, SMS_INTERVAL);
        } else {
            Toast.makeText(this, "Phone numbers not set", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSingleEmergencySMS(String phoneNumber) {
        Location userLocation = getUserLocation();

        if (userLocation != null) {
            String message = "Help! I'm in danger! Please assist. Location: " +
                    "https://www.google.com/maps?q=" + userLocation.getLatitude() +
                    "," + userLocation.getLongitude();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            // Show a toast for each SMS sent
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Emergency SMS Sent", Toast.LENGTH_SHORT).show());
        } else {
            // Show a toast for location retrieval failure
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get user location", Toast.LENGTH_SHORT).show());
        }
    }

    private Location getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location userLocation = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (userLocation == null) {
                // If getLastKnownLocation returns null, try getting location from network provider
                userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        return userLocation;
    }
}



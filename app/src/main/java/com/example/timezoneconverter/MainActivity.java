package com.example.timezoneconverter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private TextView indiaTimeTextView, outputTimeTextView, indiaSunriseTextView, indiaSunsetTextView, sunriseTextView, sunsetTextView;
    private Spinner countrySpinner;
    private TextToSpeech textToSpeech;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        indiaTimeTextView = findViewById(R.id.india_time);
        outputTimeTextView = findViewById(R.id.output_time);
        indiaSunriseTextView = findViewById(R.id.sunrise_time);
        indiaSunsetTextView = findViewById(R.id.sunset_time);
        sunriseTextView = findViewById(R.id.sunrise_time);
        sunsetTextView = findViewById(R.id.sunset_time);
        countrySpinner = findViewById(R.id.country_spinner);
        Button convertButton = findViewById(R.id.convert_button);

        // Set up the spinner with some countries/time zones
        String[] countries = {
                "Select Country", "USA", "Japan", "Germany", "Australia", "UK", "Canada"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Display India's current time and sunrise/sunset times
        displayIndiaTime();
        displayIndiaSunriseSunset();

        // Handle the convert button click
        convertButton.setOnClickListener(v -> {
            String selectedCountry = countrySpinner.getSelectedItem().toString();
            if (!selectedCountry.equals("Select Country")) {
                convertIndiaTimeToCountry(selectedCountry);
                fetchSunriseSunset(selectedCountry); // Fetch sunrise and sunset times for selected country
            }
        });
    }

    // Display current time in India
    private void displayIndiaTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String indiaTime = sdf.format(new Date());
        indiaTimeTextView.setText("India Time: " + indiaTime);
    }

    // India's latitude and longitude for sunrise/sunset calculation
    double indiaLatitude = 20.5937;  // Approximate latitude of India
    double indiaLongitude = 78.9629;  // Approximate longitude of India
    double indiaTimezoneOffset = 5.5; // IST (UTC+5:30)

    // Calculate and display India's sunrise and sunset
    private void displayIndiaSunriseSunset() {
        double[] indiaSunriseSunset = calculateTimes(indiaLatitude, indiaLongitude, indiaTimezoneOffset);

        // Format sunrise and sunset times
        String indiaSunriseTime = String.format("Sunrise: %02d:%02d", (int) indiaSunriseSunset[0], (int) ((indiaSunriseSunset[0] % 1) * 60));
        String indiaSunsetTime = String.format("Sunset: %02d:%02d", (int) indiaSunriseSunset[1], (int) ((indiaSunriseSunset[1] % 1) * 60));

        // Display sunrise and sunset for India
        indiaSunriseTextView.setText(indiaSunriseTime);
        indiaSunsetTextView.setText(indiaSunsetTime);
    }

    // Convert India's time to the selected country's time zone
    private void convertIndiaTimeToCountry(String country) {
        String timeZone = getTimeZoneFromCountry(country);

        if (timeZone != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));  // India time
            String indiaTime = sdf.format(new Date());

            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));  // Target country's time
            String convertedTime = sdf.format(new Date());

            outputTimeTextView.setText("Converted Time (" + country + "): " + convertedTime);

            // Text-to-Speech for time
            textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = textToSpeech.setLanguage(Locale.US);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Language not supported or missing data", Toast.LENGTH_SHORT).show();
                    } else {
                        speakText(indiaTimeTextView.getText().toString() + " and " + outputTimeTextView.getText().toString());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            outputTimeTextView.setText("Time zone not found for " + country);
        }
    }

    // Get time zone based on selected country
    private String getTimeZoneFromCountry(String country) {
        switch (country) {
            case "USA":
                return "America/New_York"; // Example USA time zone
            case "Japan":
                return "Asia/Tokyo"; // Japan time zone
            case "Germany":
                return "Europe/Berlin"; // Germany time zone
            case "Australia":
                return "Australia/Sydney"; // Australia time zone
            case "UK":
                return "Europe/London"; // UK time zone
            case "Canada":
                return "America/Toronto"; // Canada time zone
            default:
                return null;
        }
    }

    // Fetch sunrise and sunset times for the selected country
    public void fetchSunriseSunset(String country) {
        double latitude = 0.0, longitude = 0.0, timezoneOffset = 0.0;

        switch (country) {
            case "USA":
                latitude = 37.7749; // San Francisco
                longitude = -122.4194;
                timezoneOffset = -8; // PST
                break;
            case "Japan":
                latitude = 35.6762; // Tokyo
                longitude = 139.6503;
                timezoneOffset = 9;
                break;
            case "Germany":
                latitude = 51.1657; // Berlin
                longitude = 10.4515;
                timezoneOffset = 1;
                break;
            case "Australia":
                latitude = -33.8688; // Sydney
                longitude = 151.2093;
                timezoneOffset = 11;
                break;
            case "UK":
                latitude = 51.5074; // London
                longitude = -0.1278;
                timezoneOffset = 0;
                break;
            case "Canada":
                latitude = 45.4215; // Ottawa
                longitude = -75.6992;
                timezoneOffset = -5;
                break;
        }

        // Calculate sunrise and sunset
        double[] sunriseSunset = calculateTimes(latitude, longitude, timezoneOffset);
        sunriseTextView.setText(String.format("Sunrise: %02d:%02d", (int) sunriseSunset[0], (int) ((sunriseSunset[0] % 1) * 60)));
        sunsetTextView.setText(String.format("Sunset: %02d:%02d", (int) sunriseSunset[1], (int) ((sunriseSunset[1] % 1) * 60)));
    }

    // Sunrise and sunset calculation
    private double[] calculateTimes(double latitude, double longitude, double timezoneOffset) {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        // Constants
        double zenith = 90.833; // Official zenith angle for sunrise/sunset

        // Approximate solar noon
        double n = dayOfYear + ((6 - longitude / 15.0) / 24.0);

        // Solar mean anomaly
        double M = (357.5291 + 0.98560028 * n) % 360;

        // Equation of the center
        double C = 1.9148 * Math.sin(Math.toRadians(M)) +
                0.02 * Math.sin(Math.toRadians(2 * M)) +
                0.0003 * Math.sin(Math.toRadians(3 * M));

        // Ecliptic longitude
        double L = (M + C + 180 + 102.9372) % 360;

        // Declination of the sun
        double sinDec = Math.sin(Math.toRadians(L)) * Math.sin(Math.toRadians(23.44));
        double cosDec = Math.cos(Math.asin(sinDec));

        // Hour angle
        double cosH = (Math.cos(Math.toRadians(zenith)) - (sinDec * Math.sin(Math.toRadians(latitude)))) /
                (cosDec * Math.cos(Math.toRadians(latitude)));

        // If the hour angle is invalid, no sunrise/sunset
        if (cosH > 1 || cosH < -1) {
            return new double[]{-1, -1}; // No sunrise/sunset
        }

        double H = Math.acos(cosH); // Convert to degrees
        H = Math.toDegrees(H);

        // Solar noon in fractional days
        double solarNoon = (720 - (4 * longitude) + (60 * timezoneOffset)) / 1440.0;

        // Sunrise and sunset in fractional days
        double sunrise = solarNoon - H / 360.0;
        double sunset = solarNoon + H / 360.0;

        // Convert fractional day to hours
        sunrise = sunrise * 24;
        sunset = sunset * 24;

        return new double[]{sunrise, sunset};
    }

    public void speakText(String text) {
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}

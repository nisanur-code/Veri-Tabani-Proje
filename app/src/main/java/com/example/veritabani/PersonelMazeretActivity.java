package com.example.veritabani;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class PersonelMazeretActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personel_mazeret);

        Spinner spn = findViewById(R.id.spnPersonel);

        // Örnek personel listesi
        String[] personeller = {"Dr. Ahmet Yılmaz", "Arş. Gör. Mehmet Demir", "Doç. Dr. Ayşe Kaya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personeller);
        spn.setAdapter(adapter);
    }
}
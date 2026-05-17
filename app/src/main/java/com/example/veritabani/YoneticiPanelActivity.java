package com.example.veritabani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class YoneticiPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yonetici_panel);

        Button btnProgram = findViewById(R.id.btnProgramOlustur);
        Button btnSalon = findViewById(R.id.btnSalonIslemleri);
        Button btnDers = findViewById(R.id.btnDersIslemleri);
        Button btnPersonel = findViewById(R.id.btnPersonelIslemleri);
        Button btnCikis = findViewById(R.id.btnCikisYap);

        // Modül 1.1: Sınav Programı (Otomatik Atama Ekranı)
        btnProgram.setOnClickListener(v -> {
            startActivity(new Intent(this, SinavEkleActivity.class));
        });

        // Modül 1.2: Salon Tanımları
        btnSalon.setOnClickListener(v -> {
            startActivity(new Intent(this, SalonPersonelActivity.class));
        });

        // Modül 1.3: Bölüm ve Ders İşlemleri (Bölüm/Yarıyıl Seçimi)
        btnDers.setOnClickListener(v -> {
            startActivity(new Intent(this, DersIslemleriActivity.class));
        });

        // Modül 1.4 & 1.5: Personel ve Mazeret Kaydı
        btnPersonel.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonelMazeretActivity.class));
        });

        btnCikis.setOnClickListener(v -> finish());
    }
}
package com.example.veritabani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etKullaniciAdi, etSifre;
    private Button btnYoneticiGirisi, btnGozetmenGirisi, btnKayitOlGit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML nesnelerini Java'ya bağlıyoruz
        etKullaniciAdi = findViewById(R.id.etKullaniciAdi);
        etSifre = findViewById(R.id.etSifre);
        btnYoneticiGirisi = findViewById(R.id.btnYoneticiGirisi);
        btnGozetmenGirisi = findViewById(R.id.btnGozetmenGirisi);
        btnKayitOlGit = findViewById(R.id.btnKayitOlGit);

        // Yönetici Giriş Butonu
        btnYoneticiGirisi.setOnClickListener(v -> {
            String kullanici = etKullaniciAdi.getText().toString();
            String sifre = etSifre.getText().toString();

            if (kullanici.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            } else {
                // Şimdilik direkt geçiş yapıyoruz, ileride SQL sorgusu eklenebilir
                Intent intent = new Intent(MainActivity.this, YoneticiPanelActivity.class);
                startActivity(intent);
            }
        });

        // Gözetmen Giriş Butonu
        btnGozetmenGirisi.setOnClickListener(v -> {
            String kullanici = etKullaniciAdi.getText().toString();
            String sifre = etSifre.getText().toString();

            if (kullanici.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, GozetmenPanelActivity.class);
                startActivity(intent);
            }
        });

        // Kayıt Ol Seçim Ekranına Yönlendirme
        btnKayitOlGit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KayitSecimActivity.class);
            startActivity(intent);
        });
    }
}
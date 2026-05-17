package com.example.veritabani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class KayitSecimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_secim); // Daha önce oluşturduğumuz XML

        Button btnGozetmenKayit = findViewById(R.id.btnGozetmenKayitGit);
        Button btnYoneticiKayit = findViewById(R.id.btnYoneticiKayitGit);

        // Gözetmen Kaydı Seçilirse
        btnGozetmenKayit.setOnClickListener(v -> {
            Intent intent = new Intent(KayitSecimActivity.this, KayitFormActivity.class);
            intent.putExtra("ROL", "Gozetmen"); // Hangi rolü seçtiğimizi gönderiyoruz
            startActivity(intent);
        });

        // Yönetici Kaydı Seçilirse
        btnYoneticiKayit.setOnClickListener(v -> {
            Intent intent = new Intent(KayitSecimActivity.this, KayitFormActivity.class);
            intent.putExtra("ROL", "Yonetici"); // Hangi rolü seçtiğimizi gönderiyoruz
            startActivity(intent);
        });
    }
}
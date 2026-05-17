package com.example.veritabani;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class KayitFormActivity extends AppCompatActivity {

    private EditText etKullaniciAdi, etEposta, etSifre;
    private Button btnKaydet;
    private TextView tvBaslik;
    private String secilenRol;
    // Bilgisayarının IP adresini buraya yazmalısın (örn: 192.168.1.xx)
    private final String KAYIT_URL = "http://10.0.2.2:5000/kayit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_form);

        etKullaniciAdi = findViewById(R.id.etKayitKullaniciAdi);
        etEposta = findViewById(R.id.etKayitEposta);
        etSifre = findViewById(R.id.etKayitSifre);
        btnKaydet = findViewById(R.id.btnKaydiTamamla);
        tvBaslik = findViewById(R.id.tvKayitBaslik);

        // Bir önceki ekrandan gönderilen ROL bilgisini alıyoruz
        secilenRol = getIntent().getStringExtra("ROL");
        tvBaslik.setText(secilenRol + " Kayıt Formu");

        btnKaydet.setOnClickListener(v -> {
            kayitOlustur();
        });
    }

    private void kayitOlustur() {
        String kullaniciAdi = etKullaniciAdi.getText().toString().trim();
        String eposta = etEposta.getText().toString().trim();
        String sifre = etSifre.getText().toString().trim();

        if (kullaniciAdi.isEmpty() || eposta.isEmpty() || sifre.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm boşlukları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Python API'ye gönderilecek JSON verisi
        JSONObject jsonVeri = new JSONObject();
        try {
            jsonVeri.put("kullanici_adi", kullaniciAdi);
            jsonVeri.put("eposta", eposta);
            jsonVeri.put("sifre", sifre);
            jsonVeri.put("rol", secilenRol);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, KAYIT_URL, jsonVeri,
                response -> {
                    Toast.makeText(KayitFormActivity.this, "Kayıt Başarılı!", Toast.LENGTH_LONG).show();
                    finish(); // Kayıt bittince giriş ekranına döner
                },
                error -> {
                    Log.e("KAYIT_HATA", "Hata: " + error.getMessage());
                    Toast.makeText(KayitFormActivity.this, "Kayıt yapılamadı, API kontrol edin!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }
}

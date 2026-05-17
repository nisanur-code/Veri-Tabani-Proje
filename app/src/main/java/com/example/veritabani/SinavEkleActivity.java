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

/**
 * SinavEkleActivity: Modül 2 kapsamındaki Akıllı Salon Atama algoritmasını ve
 * veri kayıt süreçlerini yöneten sınıftır.
 */
public class SinavEkleActivity extends AppCompatActivity {

    private EditText etOgrenciSayisi, etDersAdi;
    private TextView tvOnerilenSalonlar;
    private Button btnSalonSorgula, btnSinavKaydet;

    // Python API adresi (CMD -> ipconfig üzerinden IPv4 adresini buraya yazmalısın)
    private final String API_URL = "http://10.0.2.2:5000/sinavlar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav_ekle);

        etOgrenciSayisi = findViewById(R.id.etOgrenciSayisi);
        etDersAdi = findViewById(R.id.etDersAdi);
        tvOnerilenSalonlar = findViewById(R.id.tvOnerilenSalonlar);
        btnSalonSorgula = findViewById(R.id.btnSalonSorgula);
        btnSinavKaydet = findViewById(R.id.btnSinavKaydet);

        // MODÜL 2: OPTİMİZASYON SORGUSU
        btnSalonSorgula.setOnClickListener(v -> {
            String sayiStr = etOgrenciSayisi.getText().toString();
            if (!sayiStr.isEmpty()) {
                int sayi = Integer.parseInt(sayiStr);
                tvOnerilenSalonlar.setText("Önerilen Mekan: " + akilliSalonBul(sayi));
            } else {
                Toast.makeText(this, "Kapasite verisi giriniz!", Toast.LENGTH_SHORT).show();
            }
        });

        // VERİTABANI KAYIT SÜRECİ
        btnSinavKaydet.setOnClickListener(v -> sqlVeriKaydet());
    }

    /**
     * Python REST API üzerinden SQL Server'a veriyi POST metodu ile gönderir.
     */
    private void sqlVeriKaydet() {
        String ders = etDersAdi.getText().toString();
        String kontenjan = etOgrenciSayisi.getText().toString();

        if (ders.isEmpty() || kontenjan.isEmpty()) {
            Toast.makeText(this, "Eksik alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("ders_adi", ders);
            postData.put("kontenjan", kontenjan);
            postData.put("tarih", "2026-05-11"); // Örnek tarih
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, postData,
                response -> {
                    // Başarılı kayıt durumunda kullanıcı bilgilendirilir.
                    Toast.makeText(this, "SQL Server: Kayıt Başarılı!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("API_ERROR", error.toString());
                    Toast.makeText(this, "Bağlantı Hatası: API Kapalı olabilir!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    /**
     * Akıllı Atama Mantığı: Veritabanındaki salon kapasitelerini simüle eder.
     */
    private String akilliSalonBul(int sayi) {
        if (sayi <= 30) return "Laboratuvar-1 (30 Kişilik)";
        else if (sayi <= 60) return "Derslik-202 (60 Kişilik)";
        else return "Amfi-A (150 Kişilik)";
    }
}
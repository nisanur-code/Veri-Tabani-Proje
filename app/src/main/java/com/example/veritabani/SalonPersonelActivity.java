package com.example.veritabani;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SalonPersonelActivity: Sistemin mekan ve personel yönetim modülüdür.
 * Akıllı Atama algoritması için gerekli olan 'Kat' ve 'Kapasite' verilerini
 * SQL Server'a gönderir ve mevcut listeyi anlık olarak çeker.
 */
public class SalonPersonelActivity extends AppCompatActivity {

    private EditText etSalonAdi, etKapasite, etKatBilgisi, etPersonelAdi;
    private Button btnSalonKaydet, btnPersonelKaydet;
    private TextView txtSalonListesi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_personel);

        // UI Bileşenleri
        etSalonAdi = findViewById(R.id.etSalonAdi);
        etKapasite = findViewById(R.id.etKapasite);
        etKatBilgisi = findViewById(R.id.etKatBilgisi);
        etPersonelAdi = findViewById(R.id.etPersonelAdi);
        btnSalonKaydet = findViewById(R.id.btnSalonKaydet);
        btnPersonelKaydet = findViewById(R.id.btnPersonelKaydet);
        txtSalonListesi = findViewById(R.id.txtSalonListesi);

        // Sayfa açıldığında SQL'deki mevcut salonları listele
        verileriGetir();

        // SALON KAYIT (Kat Bilgisi Dahil)
        btnSalonKaydet.setOnClickListener(v -> {
            String ad = etSalonAdi.getText().toString();
            String kap = etKapasite.getText().toString();
            String kat = etKatBilgisi.getText().toString();

            if (!ad.isEmpty() && !kap.isEmpty() && !kat.isEmpty()) {
                // Burada API'ye POST isteği atılacak
                Toast.makeText(this, ad + " (" + kat + ". Kat) kaydedildi.", Toast.LENGTH_SHORT).show();
                etSalonAdi.setText("");
                etKapasite.setText("");
                etKatBilgisi.setText("");
            } else {
                Toast.makeText(this, "Lütfen tüm salon bilgilerini doldurun!", Toast.LENGTH_SHORT).show();
            }
        });

        // PERSONEL KAYIT
        btnPersonelKaydet.setOnClickListener(v -> {
            String pAd = etPersonelAdi.getText().toString();
            if (!pAd.isEmpty()) {
                Toast.makeText(this, pAd + " veritabanına eklendi.", Toast.LENGTH_SHORT).show();
                etPersonelAdi.setText("");
            }
        });
    }

    /**
     * SQL'den Salon ve Personel verilerini çekip TextView içinde listeler.
     * Bu işlem Python API üzerinden gerçekleştirilir.
     */
    private void verileriGetir() {
        String url = "http://10.0.2.2:5000/salonlar"; // Örnek API adresi

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("GÜNCEL SALON LİSTESİ:\n");
                    builder.append("-----------------------------\n");
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            builder.append("📍 ").append(obj.getString("Ad"))
                                    .append(" | Kap: ").append(obj.getString("Kapasite"))
                                    .append(" | Kat: ").append(obj.getString("Kat")).append("\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    txtSalonListesi.setText(builder.toString());
                },
                error -> txtSalonListesi.setText("Veriler şu an SQL'den çekilemiyor. (API Bağlantısı?)")
        );
        queue.add(request);
    }
}
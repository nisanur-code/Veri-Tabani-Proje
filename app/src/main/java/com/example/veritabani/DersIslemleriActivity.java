package com.example.veritabani;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// KIRMIZI ÇİZGİLERİ SÖNDÜRECEK OLAN EKSİK IMPORT SATIRLARI (HİÇBİRİNE DOKUNMA)
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DersIslemleriActivity extends AppCompatActivity {

    private TextView txtSonuc;
    private RequestQueue requestQueue;

    // Emülatörden bilgisayardaki Python API'ye erişim IP'si
    private final String BASE_URL = "http://10.0.2.2:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ders_islemleri);

        // Volley İstek Kuyruğunu Başlatıyoruz
        requestQueue = Volley.newRequestQueue(this);

        Spinner spnBolum = findViewById(R.id.spnBolumler);
        Spinner spnDonem = findViewById(R.id.spnDonemler);
        EditText etKodu = findViewById(R.id.etDersKodu);
        EditText etAd = findViewById(R.id.etDersAdi);
        EditText etKont = findViewById(R.id.etDersKontenjan);
        Button btnKaydet = findViewById(R.id.btnDersKaydet);
        txtSonuc = findViewById(R.id.txtSonuclar);

        // HFTTF Bölümleri ve 8 Yarıyıl
        String[] bolumler = {"Elektrik Müh.", "Enerji Sistemleri Müh.", "Mekatronik Müh.", "Makine Müh.", "Yazılım Müh."};
        String[] donemler = {"1. Yarıyıl", "2. Yarıyıl", "3. Yarıyıl", "4. Yarıyıl", "5. Yarıyıl", "6. Yarıyıl", "7. Yarıyıl", "8. Yarıyıl"};

        spnBolum.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bolumler));
        spnDonem.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, donemler));

        // EKRAN AÇILDIĞINDA SQL'DEKİ SALONLARI ÇEKMEK İÇİN FONKSİYONU ÇAĞIRIYORUZ
        salonlariSQLdenCek();

        // KAYDET BUTONUNA BASILDIĞINDA
        btnKaydet.setOnClickListener(v -> {
            String bolum = spnBolum.getSelectedItem().toString();
            int yariyil = spnDonem.getSelectedItemPosition() + 1;
            String kodu = etKodu.getText().toString().trim();
            String ad = etAd.getText().toString().trim();
            String kont = etKont.getText().toString().trim();

            if (!kodu.isEmpty() && !ad.isEmpty() && !kont.isEmpty()) {
                // Python'a göndermek üzere verileri hazırlıyoruz
                dersiSQLdeKaydet(bolum, yariyil, kodu, ad, kont);

                // Formu temizle
                etKodu.setText(""); etAd.setText(""); etKont.setText("");
            } else {
                Toast.makeText(this, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 1. VERİ GÖNDERME (POST) - Yeni Dersi Python Üzerinden SQL'e Yazar
    private void dersiSQLdeKaydet(String bolum, int yariyil, String kodu, String ad, String kont) {
        String url = BASE_URL + "/ders-ekle";

        JSONObject jsonVeri = new JSONObject();
        try {
            jsonVeri.put("bolum_ad", bolum);
            jsonVeri.put("yariyil", yariyil);
            jsonVeri.put("ders_kodu", kodu);
            jsonVeri.put("ders_adi", ad);
            jsonVeri.put("kontenjan", Integer.parseInt(kont));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonVeri,
                response -> {
                    try {
                        String mesaj = response.getString("mesaj");
                        Toast.makeText(DersIslemleriActivity.this, mesaj, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(DersIslemleriActivity.this, "Kayıt Hatası! API bağlantısını kontrol edin.", Toast.LENGTH_SHORT).show();
                    Log.e("VolleyPOST", error.toString());
                }
        );

        requestQueue.add(postRequest);
    }

    // 2. VERİ ÇEKME (GET) - SQL'deki Salonları Alttaki Alana Yazar
    private void salonlariSQLdenCek() {
        String url = BASE_URL + "/salonlar";

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Kayıtlı Salonlar:\n\n");

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject salon = response.getJSONObject(i);
                            String ad = salon.getString("ad");
                            int kapasite = salon.getInt("kapasite");
                            int kat = salon.getInt("kat");

                            sb.append("🔹 ").append(ad)
                                    .append(" (Kapasite: ").append(kapasite)
                                    .append(", Kat: ").append(kat).append(")\n");
                        }

                        txtSonuc.setText(sb.toString());

                    } catch (JSONException e) {
                        txtSonuc.setText("Veri ayrıştırma hatası.");
                        e.printStackTrace();
                    }
                },
                error -> {
                    txtSonuc.setText("Veriler şu an SQL'den çekilemiyor. (API Bağlantısı?)");
                    Log.e("VolleyGET", error.toString());
                }
        );

        requestQueue.add(getRequest);
    }
}
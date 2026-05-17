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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SinavEkleActivity extends AppCompatActivity {

    private EditText etOgrenciSayisi;
    private Spinner spDersler;
    private TextView tvOnerilenSalonlar;
    private Button btnSalonSorgula, btnSinavKaydet;

    private final String API_URL_SINAV = "http://10.0.2.2:5000/sinavlar";
    private final String API_URL_SALON = "http://10.0.2.2:5000/salonlar";
    private final String API_URL_DERS = "http://10.0.2.2:5000/dersler";

    // SQL'deki yeni tablolar için endpoint adresleri
    private final String API_URL_PERSONEL = "http://10.0.2.2:5000/personel";
    private final String API_URL_MAZERET = "http://10.0.2.2:5000/mazeretler";

    private final ArrayList<JSONObject> gercekSalonlarListesi = new ArrayList<>();
    private final ArrayList<String> dersIsimleriListesi = new ArrayList<>();

    // Gözetmen ve mazeret çakışma kontrolü için kullanılacak listeler
    private final ArrayList<JSONObject> gercekPersonelListesi = new ArrayList<>();
    private final ArrayList<JSONObject> gercekMazeretListesi = new ArrayList<>();

    private ArrayAdapter<String> dersAdapter;

    // Son sorgulamada üretilen salon kombinasyonunu saklamak için değişken
    private String sonOnerilenMekan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav_ekle);

        etOgrenciSayisi = findViewById(R.id.etOgrenciSayisi);
        spDersler = findViewById(R.id.spDersler);
        tvOnerilenSalonlar = findViewById(R.id.tvOnerilenSalonlar);
        btnSalonSorgula = findViewById(R.id.btnSalonSorgula);
        btnSinavKaydet = findViewById(R.id.btnSinavKaydet);

        dersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dersIsimleriListesi);
        dersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDersler.setAdapter(dersAdapter);

        // Uygulama açılır açılmaz tüm veritabanı tablolarını hafızaya yükler
        veritabanindanVerileriYukle();

        // ÇOKLU SALON DESTEKLİ OPTİMİZASYON SORGUSU
        btnSalonSorgula.setOnClickListener(v -> {
            String sayiStr = etOgrenciSayisi.getText().toString();
            if (!sayiStr.isEmpty()) {
                int ogrenciSayisi = Integer.parseInt(sayiStr);

                // Yeni gelişmiş bölme algoritması tetikleniyor
                sonOnerilenMekan = sqlAkilliSalonBul(ogrenciSayisi);
                tvOnerilenSalonlar.setText("Önerilen Mekan: " + sonOnerilenMekan);
            } else {
                Toast.makeText(this, "Öğrenci sayısı giriniz!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSinavKaydet.setOnClickListener(v -> sqlVeriKaydet());
    }

    private void veritabanindanVerileriYukle() {
        RequestQueue queue = Volley.newRequestQueue(this);

        // 1. ADIM: SQL'deki Gerçek Salonları Çekme
        JsonArrayRequest salonRequest = new JsonArrayRequest(Request.Method.GET, API_URL_SALON, null,
                response -> {
                    try {
                        gercekSalonlarListesi.clear();
                        for (int i = 0; i < response.length(); i++) {
                            gercekSalonlarListesi.add(response.getJSONObject(i));
                        }
                        Log.d("SQL_DATA", "1. Salonlar yüklendi. Adet: " + gercekSalonlarListesi.size());
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Log.e("SQL_HATA", "Salonlar yüklenemedi: " + error.toString())
        );
        salonRequest.setShouldCache(false); // Önbelleği kapat, canlı veri zorla

        // 2. ADIM: SQL'deki Gerçek Dersleri Çekme
        JsonArrayRequest dersRequest = new JsonArrayRequest(Request.Method.GET, API_URL_DERS, null,
                response -> {
                    try {
                        dersIsimleriListesi.clear();
                        for (int i = 0; i < response.length(); i++) {
                            dersIsimleriListesi.add(response.getString(i));
                        }
                        dersAdapter.notifyDataSetChanged();
                        Log.d("SQL_DATA", "2. Dersler yüklendi. Adet: " + dersIsimleriListesi.size());
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Log.e("SQL_HATA", "Dersler yüklenemedi: " + error.toString())
        );
        dersRequest.setShouldCache(false);

        // 3. ADIM: SQL'deki Gerçek Personel Listesini Çekme
        JsonArrayRequest personelRequest = new JsonArrayRequest(Request.Method.GET, API_URL_PERSONEL, null,
                response -> {
                    try {
                        gercekPersonelListesi.clear();
                        for (int i = 0; i < response.length(); i++) {
                            gercekPersonelListesi.add(response.getJSONObject(i));
                        }
                        Log.d("SQL_DATA", "3. Personeller yüklendi. Adet: " + gercekPersonelListesi.size());
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Log.e("SQL_HATA", "Personeller yüklenemedi: " + error.toString())
        );
        personelRequest.setShouldCache(false);

        // 4. ADIM: SQL'deki Gerçek Mazeret Listesini Çekme
        JsonArrayRequest mazeretRequest = new JsonArrayRequest(Request.Method.GET, API_URL_MAZERET, null,
                response -> {
                    try {
                        gercekMazeretListesi.clear();
                        for (int i = 0; i < response.length(); i++) {
                            gercekMazeretListesi.add(response.getJSONObject(i));
                        }
                        Log.d("SQL_DATA", "4. Mazeretler yüklendi. Adet: " + gercekMazeretListesi.size());
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Log.e("SQL_HATA", "Mazeretler yüklenemedi: " + error.toString())
        );
        mazeretRequest.setShouldCache(false);

        // Tüm istekleri sıralı olarak Volley kuyruğuna gönderiyoruz
        queue.add(salonRequest);
        queue.add(dersRequest);
        queue.add(personelRequest);
        queue.add(mazeretRequest);
    }

    /**
     * GREEDY ALGORİTMA: Öğrenci sayısı tek salonu aşarsa,
     * salonları büyükten küçüğe doğru tarayıp en optimal şekilde birleştirir.
     */
    private String sqlAkilliSalonBul(int ogrenciSayisi) {
        if (gercekSalonlarListesi.isEmpty()) {
            return "Veritabanında salon bulunamadı!";
        }

        // Salonları kapasitelerine göre BÜYÜKTEN KÜÇÜĞE sıralıyoruz
        Collections.sort(gercekSalonlarListesi, (o1, o2) -> {
            try {
                return Integer.compare(o2.getInt("kapasite"), o1.getInt("kapasite"));
            } catch (JSONException e) {
                return 0;
            }
        });

        StringBuilder atananSalonlar = new StringBuilder();
        int kalanOgrenci = ogrenciSayisi;
        int toplamAtananKapasite = 0;

        // 1. Adım: Büyük salonları doldura doldura ilerle
        for (JSONObject salon : gercekSalonlarListesi) {
            if (kalanOgrenci <= 0) break;

            try {
                String ad = salon.getString("ad");
                int kapasite = salon.getInt("kapasite");

                if (kalanOgrenci >= kapasite || (kalanOgrenci > kapasite / 2)) {
                    if (atananSalonlar.length() > 0) atananSalonlar.append(" + ");

                    atananSalonlar.append(ad).append(" (").append(kapasite).append(" Kişilik)");
                    kalanOgrenci -= kapasite;
                    toplamAtananKapasite += kapasite;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 2. Adım: Kalan az sayıda öğrenci için en küçük ideal salonu bağla
        if (kalanOgrenci > 0) {
            JSONObject enIdealKucukSalon = null;
            int enYakinKapasite = Integer.MAX_VALUE;

            for (JSONObject salon : gercekSalonlarListesi) {
                try {
                    int kapasite = salon.getInt("kapasite");
                    if (kapasite >= kalanOgrenci && kapasite < enYakinKapasite) {
                        enYakinKapasite = kapasite;
                        enIdealKucukSalon = salon;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (enIdealKucukSalon != null) {
                try {
                    if (atananSalonlar.length() > 0) atananSalonlar.append(" + ");
                    atananSalonlar.append(enIdealKucukSalon.getString("ad"))
                            .append(" (").append(enIdealKucukSalon.getInt("kapasite")).append(" Kişilik)");
                    toplamAtananKapasite += enIdealKucukSalon.getInt("kapasite");
                    kalanOgrenci = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (kalanOgrenci > 0) {
            return "Yetersiz Kapasite! Okuldaki tüm salonlar yetmiyor.";
        } else {
            return atananSalonlar.toString() + " [Toplam Kapasite: " + toplamAtananKapasite + "]";
        }
    }

    private void sqlVeriKaydet() {
        if (spDersler.getSelectedItem() == null) {
            Toast.makeText(this, "Önce geçerli bir ders seçmelisiniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        String secilenDers = spDersler.getSelectedItem().toString();
        String kontenjan = etOgrenciSayisi.getText().toString();

        if (kontenjan.isEmpty()) {
            Toast.makeText(this, "Eksik alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sonOnerilenMekan.isEmpty() || sonOnerilenMekan.contains("bulunamadı")) {
            Toast.makeText(this, "Lütfen önce geçerli bir salon sorgulaması yapın!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cihazın dinamik bugünkü tarihini alıyoruz
        String bugununkutarihi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        JSONObject postData = new JSONObject();
        try {
            postData.put("ders_adi", secilenDers);
            postData.put("kontenjan", kontenjan);
            postData.put("salon_bilgisi", sonOnerilenMekan);
            postData.put("tarih", bugununkutarihi);
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL_SINAV, postData,
                response -> {
                    Toast.makeText(this, "SQL Server: Kayıt Başarılı!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    if (error.networkResponse != null) {
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        Log.e("SQL_HATA", "Sunucu Hata Kodu: " + statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("SQL_HATA", "Detay: " + responseBody);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    Toast.makeText(this, "Bağlantı/Sunucu Hatası! Logcat kontrol edin.", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }
}
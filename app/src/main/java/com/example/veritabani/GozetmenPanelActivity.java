package com.example.veritabani;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class GozetmenPanelActivity extends AppCompatActivity {

    private RecyclerView rvSinavlar;
    private Button btnGeri;
    private Spinner spFiltreBolum, spFiltreSinif;

    private YeniSinavAdapter adapter;

    // Ekranda o an gösterilen filtrelenmiş liste
    private final ArrayList<YeniSinavModel> sinavListesi = new ArrayList<>();
    // API'den gelen tüm ham veriyi saklayan ana liste
    private final ArrayList<YeniSinavModel> tumSinavlarListesi = new ArrayList<>();

    private final String API_URL = "http://10.0.2.2:5000/sinavlar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gozetmen_panel);

        rvSinavlar = findViewById(R.id.rvSinavlar);
        btnGeri = findViewById(R.id.btnGeri);
        spFiltreBolum = findViewById(R.id.spFiltreBolum);
        spFiltreSinif = findViewById(R.id.spFiltreSinif);

        rvSinavlar.setLayoutManager(new LinearLayoutManager(this));

        adapter = new YeniSinavAdapter(sinavListesi);
        rvSinavlar.setAdapter(adapter);

        // 1. Üstteki Açılır Kutuların İçeriğini Doldur
        filtreGirdileriniHazirla();

        // 2. SQL Veritabanından Tüm Sınavları Çek
        sinavPrograminiGetir();

        // 3. Filtre Seçimleri Değiştiğinde Tetiklenecek Dinleyici
        // KESİN ÇÖZÜM: Değişken adı 'filtreListener' olarak sabitlendi
        AdapterView.OnItemSelectedListener filtreListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtreleVeListele();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        // KESİN ÇÖZÜM: filterListener hatası veren parantez içleri filtreListener yapıldı
        spFiltreBolum.setOnItemSelectedListener(filtreListener);
        spFiltreSinif.setOnItemSelectedListener(filtreListener);

        btnGeri.setOnClickListener(v -> finish());
    }

    private void filtreGirdileriniHazirla() {
        // Bölüm Filtresi (Veritabanındaki BolumID yapılandırmasına özel)
        String[] bolumler = {"Tüm Bölümler", "Yazılım Müh. (ID: 1)", "Bilgisayar Müh. (ID: 2)"};
        ArrayAdapter<String> bAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bolumler);
        bAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltreBolum.setAdapter(bAdapter);

        // Sınıf/Yarıyıl Filtresi (Dönem Karşılıkları)
        String[] yariyillar = {"Tüm Sınıflar", "1. Yarıyıl", "3. Yarıyıl", "5. Yarıyıl", "7. Yarıyıl"};
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yariyillar);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltreSinif.setAdapter(sAdapter);
    }

    private void sinavPrograminiGetir() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        tumSinavlarListesi.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // Python INNER JOIN ile gönderdiğimiz tüm veriyi modele dolduruyoruz
                            YeniSinavModel model = new YeniSinavModel(
                                    obj.getString("DersID"),
                                    obj.getString("SinavTarih")
                            );

                            // Filtreleme için yeni eklenen alanları dolduruyoruz
                            model.setDersAd(obj.getString("DersAd"));
                            model.setBolumID(obj.getInt("BolumID"));
                            model.setYariyil(obj.getInt("Yariyil"));

                            tumSinavlarListesi.add(model);
                        }

                        // Veriler ilk yüklendiğinde filtre olmadan her şeyi listele
                        filtreleVeListele();
                        Log.d("SQL_DATA", "API verisi başarıyla çekildi. Toplam: " + tumSinavlarListesi.size());

                    } catch (JSONException e) {
                        Log.e("SQL_PARSE_HATA", "JSON okuma hatası: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("SQL_HATA", "Bağlantı Hatası: " + error.getMessage());
                    Toast.makeText(this, "API bağlantısı kurulamadı!", Toast.LENGTH_SHORT).show();
                }
        );

        jsonArrayRequest.setShouldCache(false); // Önbelleği kapat
        queue.add(jsonArrayRequest);
    }

    /**
     * SÜZGEÇ FONKSİYONU:
     * Seçilen Spinner değerlerine göre listeyi anlık günceller.
     */
    private void filtreleVeListele() {
        sinavListesi.clear();

        int secilenBolumPos = spFiltreBolum.getSelectedItemPosition(); // 0: Tümü, 1: ID=1, 2: ID=2
        int secilenSinifPos = spFiltreSinif.getSelectedItemPosition(); // 0: Tümü, 1: Yarıyıl=1, 2: Yarıyıl=3...

        // Spinner pozisyonunu veritabanındaki tekli yarıyıl sayılarına çeviriyoruz (1->1, 2->3, 3->5, 4->7)
        int gercekYariyil = (secilenSinifPos == 0) ? 0 : (secilenSinifPos * 2 - 1);

        for (YeniSinavModel sinav : tumSinavlarListesi) {
            boolean bolumUyumlu = (secilenBolumPos == 0 || sinav.getBolumID() == secilenBolumPos);
            boolean sinifUyumlu = (secilenSinifPos == 0 || sinav.getYariyil() == gercekYariyil);

            if (bolumUyumlu && sinifUyumlu) {
                sinavListesi.add(sinav);
            }
        }

        // Adapter'a verilerin değiştiğini haber vererek arayüzü anlık tazele
        adapter.notifyDataSetChanged();
    }
}
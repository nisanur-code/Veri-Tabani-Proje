package com.example.veritabani;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private SinavAdapter adapter;
    private ArrayList<SinavModel> sinavListesi = new ArrayList<>();

    // Python API adresi
    private final String API_URL = "http://10.0.2.2:5000/sinavlar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gozetmen_panel);

        rvSinavlar = findViewById(R.id.rvSinavlar);
        btnGeri = findViewById(R.id.btnGeri);

        // RecyclerView yapılandırması
        rvSinavlar.setLayoutManager(new LinearLayoutManager(this));

        // Adapter'ı başta boş liste ile bağlıyoruz
        adapter = new SinavAdapter(sinavListesi);
        rvSinavlar.setAdapter(adapter);

        sinavPrograminiGetir();

        btnGeri.setOnClickListener(v -> finish());
    }

    private void sinavPrograminiGetir() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        sinavListesi.clear(); // Eski verileri temizle
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // SQL'deki kolon isimlerine göre eşleştirme (DersID, SinavTarih)
                            sinavListesi.add(new SinavModel(
                                    obj.getString("DersID"),
                                    obj.getString("SinavTarih")
                            ));
                        }

                        // Veriler geldikten sonra listeyi görsel olarak tazeliyoruz
                        adapter.notifyDataSetChanged();
                        Log.d("SQL_DATA", "Adapter güncellendi. Eleman: " + sinavListesi.size());

                    } catch (JSONException e) {
                        Log.e("SQL_PARSE_HATA", "JSON okuma hatası: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("SQL_HATA", "Bağlantı Hatası: " + error.getMessage());
                    Toast.makeText(this, "API bağlantısı kurulamadı!", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
package com.example.veritabani;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SinavAdapter extends RecyclerView.Adapter<SinavAdapter.ViewHolder> {
    private ArrayList<SinavModel> sinavlar;

    public SinavAdapter(ArrayList<SinavModel> sinavlar) {
        this.sinavlar = sinavlar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Hazır basit Android list tasarımını kullanıyoruz
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SinavModel model = sinavlar.get(position);
        holder.textView.setText("Ders No: " + model.getDersID() + " | Tarih: " + model.getTarih());
    }

    @Override
    public int getItemCount() { return sinavlar.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
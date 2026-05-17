package com.example.veritabani;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class YeniSinavAdapter extends RecyclerView.Adapter<YeniSinavAdapter.ViewHolder> {

    private ArrayList<YeniSinavModel> sinavlar;

    public YeniSinavAdapter(ArrayList<YeniSinavModel> sinavlar) {
        this.sinavlar = sinavlar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YeniSinavModel model = sinavlar.get(position);
        holder.text1.setText("Ders ID: " + model.getDersID());
        holder.text2.setText("Sınav Tarihi: " + model.getSinavTarih());
    }

    @Override
    public int getItemCount() {
        return sinavlar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
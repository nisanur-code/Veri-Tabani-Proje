package com.example.veritabani;

public class SinavModel {
    private String dersID;
    private String tarih;

    public SinavModel(String dersID, String tarih) {
        this.dersID = dersID;
        this.tarih = tarih;
    }

    public String getDersID() { return dersID; }
    public String getTarih() { return tarih; }
}

package com.example.veritabani;

public class YeniSinavModel {
    private String dersID;
    private String sinavTarih;

    // Filtreleme için yeni eklenen alanlar
    private String dersAd;
    private int bolumID;
    private int yariyil;

    // Mevcut Yapıcı Metot (Constructor)
    public YeniSinavModel(String dersID, String sinavTarih) {
        this.dersID = dersID;
        this.sinavTarih = sinavTarih;
    }

    // DersID ve SinavTarih Getter/Setter metotları
    public String getDersID() { return dersID; }
    public void setDersID(String dersID) { this.dersID = dersID; }
    public String getSinavTarih() { return sinavTarih; }
    public void setSinavTarih(String sinavTarih) { this.sinavTarih = sinavTarih; }

    // Hataları Çözen Yeni Getter ve Setter Metotları
    public String getDersAd() { return dersAd; }
    public void setDersAd(String dersAd) { this.dersAd = dersAd; }
    public int getBolumID() { return bolumID; }
    public void setBolumID(int bolumID) { this.bolumID = bolumID; }
    public int getYariyil() { return yariyil; }
    public void setYariyil(int yariyil) { this.yariyil = yariyil; }
}
from flask import Flask, jsonify, request
import pyodbc

app = Flask(__name__)

# SQL SERVER BAĞLANTI FONKSİYONU
def get_db_connection():
    try:
        conn_str = (
            'DRIVER={SQL Server};'
            'SERVER=.\\SQLEXPRESS;'
            'DATABASE=SinavSistemiDB;'
            'Trusted_Connection=yes;'
        )
        return pyodbc.connect(conn_str)
    except Exception as e:
        print(f"Bağlantı Hatası: {e}")
        return None


# MODÜL 1.3: YENİ DERS EKLE (POST)
@app.route('/ders-ekle', methods=['POST'])
def ders_ekle():
    veri = request.json
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        sorgu = "INSERT INTO dbo.DERSLER (BolumAd, DersKodu, DersAd, Kontenjan, Yariyil) VALUES (?, ?, ?, ?, ?)"
        cursor.execute(sorgu, (
            veri.get('bolum_ad'), veri.get('ders_kodu'), 
            veri.get('ders_adi'), veri.get('kontenjan'), 
            veri.get('yariyil')
        ))
        conn.commit()
        conn.close()
        return jsonify({"mesaj": "Ders başarıyla kaydedildi!"}), 201
    except Exception as e: 
        return jsonify({"hata": str(e)}), 400


# MODÜL 1.2: SALON EKLE (POST)
@app.route('/salon-ekle', methods=['POST'])
def salon_ekle():
    veri = request.json
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        sorgu = "INSERT INTO dbo.DERSLIKLER (DerslikAd, Kapasite, KatBilgisi) VALUES (?, ?, ?)"
        cursor.execute(sorgu, (veri.get('ad'), veri.get('kapasite'), veri.get('kat')))
        conn.commit()
        conn.close()
        return jsonify({"mesaj": "Salon tanımlandı!"}), 201
    except Exception as e: 
        return jsonify({"hata": str(e)}), 400


# SALONLARI ÇEKME (GET) - Android Akıllı Atama Algoritması İçin
@app.route('/salonlar', methods=['GET'])
def salonlari_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT DerslikAd, Kapasite, KatBilgisi FROM dbo.DERSLIKLER")
        satirlar = cursor.fetchall()
        salon_listesi = [{"ad": s[0].strip() if s[0] else "", "kapasite": s[1], "kat": s[2]} for s in satirlar]
        conn.close()
        return jsonify(salon_listesi), 200
    except Exception as e: 
        return jsonify({"hata": str(e)}), 400


# DERSLERİ ÇEKME (GET) - SinavEkleActivity İçindeki Spinner'ı Doldurmak İçin
@app.route('/dersler', methods=['GET'])
def dersleri_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT DersAd FROM dbo.DERSLER")
        satirlar = cursor.fetchall()
        ders_listesi = [s[0].strip() for s in satirlar if s[0]]
        conn.close()
        return jsonify(ders_listesi), 200
    except Exception as e: 
        return jsonify({"hata": str(e)}), 400


# ANDROID 'GozetmenPanelActivity' BÖLÜM/SINIF FİLTRELEMESİ İÇİN INNER JOIN SÜRÜMÜ (GET)
@app.route('/sinavlar', methods=['GET'])
def sinavlari_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        sorgu = """
            SELECT s.DersID, s.SinavTarih, d.DersAd, d.BolumID, d.Yariyil 
            FROM dbo.SINAVLAR s
            INNER JOIN dbo.DERSLER d ON s.DersID = d.DersID
        """
        cursor.execute(sorgu)
        satirlar = cursor.fetchall()
        
        sinav_listesi = []
        for s in satirlar:
            sinav_listesi.append({
                "DersID": str(s[0]),
                "SinavTarih": str(s[1]).strip() if s[1] else "",
                "DersAd": s[2].strip() if s[2] else "",
                "BolumID": s[3],
                "Yariyil": s[4]
            })
        conn.close()
        return jsonify(sinav_listesi), 200
    except Exception as e: 
        return jsonify({"hata": str(e)}), 400


# ANDROID 'SinavEkleActivity' İÇİN YENİ SINAV KAYDETME (POST)
@app.route('/sinavlar', methods=['POST'])
def sinav_ekle():
    veri = request.json
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        android_ders_adi = veri.get('ders_adi')
        sinav_tarihi = veri.get('tarih')
        
        cursor.execute("SELECT DersID FROM dbo.DERSLER WHERE RTRIM(DersAd) = RTRIM(?)", (android_ders_adi,))
        row = cursor.fetchone()
        
        if row:
            gercek_id = row[0]
        else:
            cursor.execute("SELECT TOP 1 DersID FROM dbo.DERSLER")
            gercek_id = cursor.fetchone()[0]

        sorgu = "INSERT INTO dbo.SINAVLAR (DersID, SinavTarih) VALUES (?, ?)"
        cursor.execute(sorgu, (int(gercek_id), str(sinav_tarihi)))
        
        conn.commit()
        conn.close()
        return jsonify({"mesaj": "Başarıyla Kaydedildi!"}), 201
    except Exception as e:
        print("SQL HATASI:", str(e))
        return jsonify({"hata": str(e)}), 400


# PERSONEL LİSTESİNİ ÇEKME (GET)
@app.route('/personel', methods=['GET'])
def personel_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT PersonelID, PersonelAd, Unvan FROM dbo.PERSONEL")
        satirlar = cursor.fetchall()
        
        personel_listesi = []
        for s in satirlar:
            personel_listesi.append({
                "PersonelID": s[0],
                "PersonelAd": s[1].strip() if s[1] else "",
                "Unvan": s[2].strip() if s[2] else ""
            })
        conn.close()
        return jsonify(personel_listesi), 200
    except Exception as e:
        return jsonify({"hata": str(e)}), 400


# MAZERET LİSTESİNİ ÇEKME (GET)
@app.route('/mazeretler', methods=['GET'])
def mazeretleri_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT MazeretID, PersonelID, MazeretTarih, Aciklama FROM dbo.MAZERET")
        satirlar = cursor.fetchall()
        
        mazeret_listesi = []
        for s in satirlar:
            mazeret_listesi.append({
                "MazeretID": s[0],
                "PersonelID": s[1],
                "MazeretTarih": str(s[2]).strip() if s[2] else "",
                "Aciklama": s[3].strip() if s[3] else ""
            })
        conn.close()
        return jsonify(mazeret_listesi), 200
    except Exception as e:
        return jsonify({"hata": str(e)}), 400


# UYGULAMAYI BAŞLATMA
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
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
        sorgu = """
            INSERT INTO dbo.DERSLER (BolumAd, DersKodu, Ad, Kontenjan, Yariyil) 
            VALUES (?, ?, ?, ?, ?)
        """
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
        sorgu = "INSERT INTO dbo.DERSLIKLER (Ad, Kapasite, Kat) VALUES (?, ?, ?)"
        cursor.execute(sorgu, (veri.get('ad'), veri.get('kapasite'), veri.get('kat')))
        conn.commit()
        conn.close()
        return jsonify({"mesaj": "Salon tanımlandı!"}), 201
    except Exception as e:
        return jsonify({"hata": str(e)}), 400


# YENİ EKLENEN KISIM - EKRENDEKİ LİSTEYİ DOLDURMAK İÇİN SALONLARI ÇEKME (GET)
@app.route('/salonlar', methods=['GET'])
def salonlari_getir():
    conn = get_db_connection()
    if conn is None: 
        return jsonify({"hata": "SQL bağlantısı yok"}), 500
    try:
        cursor = conn.cursor()
        # SQL'deki tablodan verileri seçiyoruz
        cursor.execute("SELECT Ad, Kapasite, Kat FROM dbo.DERSLIKLER")
        satirlar = cursor.fetchall()
        
        # SQL tablosundaki satırları, telefonun anlayacağı yazı diline (listeye) çeviriyoruz
        salon_listesi = []
        for satir in satirlar:
            salon_listesi.append({
                "ad": satir[0],
                "kapasite": satir[1],
                "kat": satir[2]
            })
        
        conn.close()
        return jsonify(salon_listesi), 200 # Telefona verileri paketleyip gönderiyoruz
    except Exception as e:
        return jsonify({"hata": str(e)}), 400


# UYGULAMAYI BAŞLATMA
if __name__ == '__main__':
    # host='0.0.0.0' sayesinde emülatör bu koda erişebilir
    app.run(host='0.0.0.0', port=5000, debug=True)
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class VeterinerSistemi {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/veteriner_sistemi";
    static final String USER = "root";
    static final String PASS = "1234";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Bağlantı başarılı.");

            while (true) {
                System.out.println("1. Hekimleri Görüntüle");
                System.out.println("2. Hastaları Görüntüle");
                System.out.println("3. İlaçları Görüntüle");
                System.out.println("4. Randevu Bilgilerini Görüntüle");
                System.out.println("5. Yeni Randevu Ekle");
                System.out.println("6. Yeni İlaç Ekle");
                System.out.println("7. Çıkış");
                System.out.print("Bir seçenek girin: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        hekimleriGoruntule(conn);
                        break;
                    case 2:
                        hastalariGoruntule(conn);
                        break;
                    case 3:
                        ilaclariGoruntule(conn);
                        break;
                    case 4:
                    	randevuBilgileriniGoruntule(conn);
                    case 5:
                        yeniRandevuEkle(conn, scanner);
                        break;
                    case 6:
                        yeniIlacEkle(conn, scanner);
                        break;
                    case 7:
                        System.out.println("Programdan çıkılıyor...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Geçersiz seçenek! Tekrar deneyin.");
                        break;
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            scanner.close();
        }
    }

    private static void hekimleriGoruntule(Connection conn) throws SQLException {
    	System.out.println("Hekimler");

        String sql = "SELECT * FROM Hekimler";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int hekimID = rs.getInt("HekimID");
                String ad = rs.getString("Ad");
                String soyad = rs.getString("Soyad");
                String uzmanlik = rs.getString("UzmanlikAlani");
                String telefon = rs.getString("TelefonNumarasi");
                String eposta = rs.getString("Eposta");

                System.out.println("Hekim ID: " + hekimID);
                System.out.println("Ad: " + ad);
                System.out.println("Soyad: " + soyad);
                System.out.println("Uzmanlık Alanı: " + uzmanlik);
                System.out.println("Telefon Numarası: " + telefon);
                System.out.println("Eposta: " + eposta);
                System.out.println("----------------------");
            }
        }
    }

    private static void hastalariGoruntule(Connection conn) throws SQLException {
    	System.out.println("Hastalar");

        String sql = "SELECT * FROM Hastalar";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int hastaID = rs.getInt("HastaID");
                String ad = rs.getString("Ad");
                String tur = rs.getString("Tür");
                int yas = rs.getInt("Yaş");
                String cinsiyet = rs.getString("Cinsiyet");
                int sahipID = rs.getInt("SahipID");

                System.out.println("Hasta ID: " + hastaID);
                System.out.println("Ad: " + ad);
                System.out.println("Tür: " + tur);
                System.out.println("Yaş: " + yas);
                System.out.println("Cinsiyet: " + cinsiyet);
                System.out.println("Sahip ID: " + sahipID);
                System.out.println("----------------------");
            }
        }
    }

    private static void ilaclariGoruntule(Connection conn) throws SQLException {
    	System.out.println("İlaçlar");

        String sql = "SELECT * FROM İlaçlar";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ilacAdi = rs.getString("İlaçAdı");
                String dozaj = rs.getString("Dozaj");
                String ilacTuru = rs.getString("İlaçTürü");

                System.out.println("İlaç Adı: " + ilacAdi);
                System.out.println("Dozaj: " + dozaj);
                System.out.println("İlaç Türü: " + ilacTuru);
                System.out.println("----------------------");
            }
        }
    }


    private static void yeniRandevuEkle(Connection conn, Scanner scanner) throws SQLException {
    	System.out.println("Yeni Randevu Ekle");
    	
        System.out.print("Hekim ID: ");
        int hekimID = scanner.nextInt();
        System.out.print("Hasta ID: ");
        int hastaID = scanner.nextInt();
        System.out.print("Randevu Tarihi ve Saati (YYYY-MM-DD HH:mm:ss): ");
        String tarihSaatStr = scanner.next();
        LocalDateTime tarihSaat = LocalDateTime.parse(tarihSaatStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.print("Notlar: ");
        String notlar = scanner.next();

        String sql = "INSERT INTO Randevular (HekimID, HastaID, TarihSaat, Notlar) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, hekimID);
            preparedStatement.setInt(2, hastaID);
            preparedStatement.setObject(3, tarihSaat);
            preparedStatement.setString(4, notlar);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Yeni randevu başarıyla eklendi.");
            } else {
                System.out.println("Hata! Randevu eklenemedi.");
            }
        }
    }

    private static void yeniIlacEkle(Connection conn, Scanner scanner) throws SQLException {
    	System.out.println("Yeni Ilac Ekle");

        System.out.print("Ilac Adi: ");
        String ilacAdi = scanner.next();
        System.out.print("Dozaj: ");
        String dozaj = scanner.next();
        System.out.print("Ilac Turu: ");
        String ilacTuru = scanner.next();

        String sql = "INSERT INTO İlaçlar (İlaçAdı, Dozaj, İlaçTürü) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, ilacAdi);
            preparedStatement.setString(2, dozaj);
            preparedStatement.setString(3, ilacTuru);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Yeni ilac başarıyla eklendi.");
            } else {
                System.out.println("Hata! Ilac eklenemedi.");
            }
        }
    }
    private static void randevuBilgileriniGoruntule(Connection conn) throws SQLException {
        System.out.println("Randevu Bilgileri");

        String sql = "SELECT * FROM Randevular";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int randevuID = rs.getInt("RandevuID");
                int hekimID = rs.getInt("HekimID");
                int hastaID = rs.getInt("HastaID");
                LocalDateTime tarihSaat = rs.getObject("TarihSaat", LocalDateTime.class);
                String notlar = rs.getString("Notlar");

                System.out.println("Randevu ID: " + randevuID);
                System.out.println("Hekim ID: " + hekimID);
                System.out.println("Hasta ID: " + hastaID);
                System.out.println("Tarih ve Saat: " + tarihSaat);
                System.out.println("Notlar: " + notlar);
                System.out.println("----------------------");
            }
        }
    }
}


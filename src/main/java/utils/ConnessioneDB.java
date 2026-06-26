package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDB {
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Approccio universale compatibile al 100% con i moduli Java ed Eclipse
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Parametri presi dal tuo context.xml
            String url = "jdbc:mysql://localhost:3306/tokyobites?allowPublicKeyRetrieval=true&useSSL=false";
            String username = "root";
            String password = "TokyoBites2026!";
            
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connessione stabilita con successo via DriverManager (Aggirato problema di Build Path)!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trovato! Assicurati che il file mysql-connector-j-*.jar sia dentro la cartella WEB-INF/lib");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Errore SQL: Credenziali errate o il server MySQL è spento.");
            e.printStackTrace();
        }
        return conn;
    }
}
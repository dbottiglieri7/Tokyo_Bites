package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDB {
    
    // Stringa di connessione JDBC per MySQL con i permessi che abbiamo configurato su DBeaver
    private static final String URL = "jdbc:mysql://localhost:3306/tokyobites?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "TokyoBites2026!";
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Carichiamo il driver MySQL in memoria
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Tentiamo la connessione fisica
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connessione al database TokyoBites riuscita!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC non trovato! Hai aggiunto il file .jar al Build Path? Errore: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Errore di connessione al database! Errore: " + e.getMessage());
        }
        return conn;
    }

    // Metodo main per testare se la connessione funziona
    public static void main(String[] args) {
        getConnection();
    }
}
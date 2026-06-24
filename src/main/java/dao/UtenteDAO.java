package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Utente;
import utils.ConnessioneDB;

public class UtenteDAO {

    // 1. Metodo per registrare un nuovo utente nel database
    public boolean registraUtente(Utente u) {
        String query = "INSERT INTO utente (email, password, nome, cognome, ruolo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPassword()); // In un progetto reale andrebbe criptata, ma per l'esame va benissimo in chiaro
            ps.setString(3, u.getNome());
            ps.setString(4, u.getCognome());
            ps.setString(5, u.getRuolo() != null ? u.getRuolo() : "CLIENTE"); // Default cliente se non specificato
            
            int righeInserite = ps.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Errore in UtenteDAO.registraUtente: " + e.getMessage());
            return false;
        }
    }

    // 2. Metodo per verificare il Login (cerca email e password nel DB)
    public Utente login(String email, String password) {
        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";
        
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utente u = new Utente();
                    u.setId(rs.getInt("id"));
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password"));
                    u.setNome(rs.getString("nome"));
                    u.setCognome(rs.getString("cognome"));
                    u.setRuolo(rs.getString("ruolo"));
                    return u; // Login riuscito, restituisce l'utente completo
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore in UtenteDAO.login: " + e.getMessage());
        }
        return null; // Login fallito (utente o password errati)
    }

    // Main di test per provare SUBITO a registrare un utente finto!
    public static void main(String[] args) {
        UtenteDAO dao = new UtenteDAO();
        
        // Creiamo un utente di test
        Utente testUser = new Utente(0, "mario.rossi@email.com", "password123", "Mario", "Rossi", "CLIENTE");
        
        System.out.println("--- Test Registrazione ---");
        boolean registrato = dao.registraUtente(testUser);
        if (registrato) {
            System.out.println(" Utente registrato con successo nel database!");
        } else {
            System.out.println("❌ Registrazione fallita (magari l'email esiste già?)");
        }
        
        System.out.println("\n--- Test Login ---");
        Utente loggato = dao.login("mario.rossi@email.com", "password123");
        if (loggato != null) {
            System.out.println(" Login riuscito! Benvenuto " + loggato.getNome() + " (" + loggato.getRuolo() + ")");
        } else {
            System.out.println("❌ Login fallito: credenziali errate.");
        }
    }
}
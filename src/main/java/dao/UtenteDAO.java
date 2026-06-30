package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Utente;
import javax.sql.DataSource; // Import necessario per gestire il Connection Pool tramite JNDI

public class UtenteDAO {

    // Riferimento al DataSource locale del DAO
    private DataSource ds;

    // Costruttore che riceve il DataSource dalla Servlet
    public UtenteDAO(DataSource ds) {
        this.ds = ds;
    }

    // 1. Metodo per registrare un nuovo utente nel database
    public boolean registraUtente(Utente u) {
        String query = "INSERT INTO utente (email, password, nome, cognome, ruolo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = (this.ds != null) ? this.ds.getConnection() : null;
             PreparedStatement ps = (conn != null) ? conn.prepareStatement(query) : null) {
            
            if (ps == null) throw new SQLException("Impossibile connettersi al database (DataSource nullo).");
            
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
        
        try (Connection conn = (this.ds != null) ? this.ds.getConnection() : null;
             PreparedStatement ps = (conn != null) ? conn.prepareStatement(query) : null) {
            
            if (ps == null) throw new SQLException("Impossibile connettersi al database (DataSource nullo).");
            
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
}
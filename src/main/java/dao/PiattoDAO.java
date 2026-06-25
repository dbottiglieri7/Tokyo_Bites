package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Piatto;
import utils.ConnessioneDB;

public class PiattoDAO {

    // Recupera TUTTI i piatti dal database
    public List<Piatto> getAllPiatti() {
        List<Piatto> lista = new ArrayList<>();
        String query = "SELECT * FROM prodotto";

        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Piatto p = new Piatto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setDescrizione(rs.getString("descrizione"));
                p.setPrezzo(rs.getDouble("prezzo"));
                p.setCategoria(rs.getString("categoria"));
                p.setImmagine(rs.getString("immagine"));
                lista.add(p);
            }
            System.out.println("PiattoDAO: Recuperati " + lista.size() + " piatti totali dal database.");

        } catch (SQLException e) {
            System.err.println("❌ Errore in PiattoDAO.getAllPiatti: " + e.getMessage());
        }
        return lista;
    }

    // Recupera i piatti filtrati per categoria
    public List<Piatto> getPiattiByCategoria(String categoria) {
        List<Piatto> lista = new ArrayList<>();
        String query = "SELECT * FROM prodotto WHERE categoria = ?";

        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Piatto p = new Piatto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setPrezzo(rs.getDouble("prezzo"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setImmagine(rs.getString("immagine"));
                    lista.add(p);
                }
            }
            System.out.println("PiattoDAO: Recuperati " + lista.size() + " piatti per la categoria: " + categoria);

        } catch (SQLException e) {
            System.err.println("❌ Errore in PiattoDAO.getPiattiByCategoria: " + e.getMessage());
        }
        return lista;
    }
    
    // Recupera un singolo piatto tramite la sua chiave primaria
    public Piatto doRetrieveByKey(int id) throws SQLException {
        String query = "SELECT * FROM prodotto WHERE id = ?";
        
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Piatto p = new Piatto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setPrezzo(rs.getDouble("prezzo"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setImmagine(rs.getString("immagine"));
                    return p;
                }
            }
        }
        return null; 
    }

    // ==========================================
    // METODI METODI AGGIUNTI PER L'AMMINISTRATORE
    // ==========================================

    // Inserisce un nuovo piatto nel database (Create)
    public void doSave(Piatto piatto) throws SQLException {
        String query = "INSERT INTO prodotto (nome, descrizione, prezzo, categoria, immagine) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, piatto.getNome());
            ps.setString(2, piatto.getDescrizione() != null ? piatto.getDescrizione() : "");
            ps.setDouble(3, piatto.getPrezzo());
            ps.setString(4, piatto.getCategoria());
            ps.setString(5, piatto.getImmagine() != null ? piatto.getImmagine() : "default.png");
            ps.executeUpdate();
            System.out.println("PiattoDAO: Nuovo piatto inserito con successo.");
        }
    }

    // Modifica un piatto esistente (Update)
    public void doUpdate(Piatto piatto) throws SQLException {
        String query = "UPDATE prodotto SET nome = ?, prezzo = ?, categoria = ? WHERE id = ?";
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, piatto.getNome());
            ps.setDouble(2, piatto.getPrezzo());
            ps.setString(3, piatto.getCategoria());
            ps.setInt(4, piatto.getId());
            ps.executeUpdate();
            System.out.println("PiattoDAO: Piatto con ID " + piatto.getId() + " aggiornato con successo.");
        }
    }

    // Cancella un piatto (Delete) 
    public void doDeleteLogico(int id) throws SQLException {
        String query = "DELETE FROM prodotto WHERE id = ?"; 
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("PiattoDAO: Piatto con ID " + id + " rimosso dal catalogo.");
        }
    }

    // Metodo Main per test rapidi
    public static void main(String[] args) {
        PiattoDAO dao = new PiattoDAO();
        List<Piatto> piatti = dao.getPiattiByCategoria("Bevande");
        System.out.println("\n--- Lista Piatti (Bevande) stampata da Java ---");
        for (Piatto p : piatti) {
            System.out.println(p.getNome() + " - " + p.getPrezzo() + "€");
        }
    }
}
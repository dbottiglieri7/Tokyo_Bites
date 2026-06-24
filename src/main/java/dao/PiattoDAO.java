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

    // NUOVO METODO: Recupera i piatti filtrati per categoria
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
    
 // AGGIUNGI QUESTO METODO DENTRO PiattoDAO.java
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
        return null; // Ritorna null se non trova nessun piatto con quell'ID
    }

    public static void main(String[] args) {
        PiattoDAO dao = new PiattoDAO();
        // Test del nuovo metodo con una categoria di esempio
        List<Piatto> piatti = dao.getPiattiByCategoria("Bevande");
        System.out.println("\n--- Lista Piatti (Bevande) stampata da Java ---");
        for (Piatto p : piatti) {
            System.out.println(p.getNome() + " - " + p.getPrezzo() + "€");
        }
    }
}
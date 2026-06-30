package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Piatto;
import javax.sql.DataSource; // Import necessario per la gestione JNDI / Tomcat 11

public class PiattoDAO {

    // Riferimento al DataSource locale del DAO
    private DataSource ds;

    // Costruttore che riceve il DataSource dalla Servlet
    public PiattoDAO(DataSource ds) {
        this.ds = ds;
    }

    // Recupera TUTTI i piatti dal database
    public List<Piatto> getAllPiatti() {
        List<Piatto> lista = new ArrayList<>();
        String query = "SELECT * FROM prodotto";

        try (Connection conn = (this.ds != null) ? this.ds.getConnection() : null;
             PreparedStatement ps = (conn != null) ? conn.prepareStatement(query) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) throw new SQLException("Impossibile connettersi al database (DataSource nullo).");

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

        try (Connection conn = (this.ds != null) ? this.ds.getConnection() : null;
             PreparedStatement ps = (conn != null) ? conn.prepareStatement(query) : null) {
            
            if (ps == null) throw new SQLException("Impossibile connettersi al database (DataSource nullo).");
            
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
        } catch (SQLException e) {
            System.err.println("❌ Errore in PiattoDAO.getPiattiByCategoria: " + e.getMessage());
        }
        return lista;
    }

    // Recupera i dettagli storici dei piatti legati a un ordine specifico
    public List<Piatto> doRetrievePiattiByOrdine(int idOrdine) throws SQLException {
        List<Piatto> listaPiatti = new ArrayList<>();
        String query =
            "SELECT p.nome, ro.prezzo_acquisto, ro.quantita " +
            "FROM riga_ordine ro " +
            "INNER JOIN prodotto p ON ro.id_prodotto = p.id " +
            "WHERE ro.id_ordine = ?";

        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Piatto p = new Piatto();
                    int quantita = rs.getInt("quantita");
                    p.setNome(rs.getString("nome") + " x" + quantita);
                    p.setPrezzo(rs.getDouble("prezzo_acquisto"));
                    listaPiatti.add(p);
                }
            }
        }
        return listaPiatti;
    }
    
    // Recupera un singolo piatto tramite la sua chiave primaria
    public Piatto doRetrieveByKey(int id) throws SQLException {
        String query = "SELECT * FROM prodotto WHERE id = ?";
        
        try (Connection conn = this.ds.getConnection();
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
    // METODI AGGIUNTI PER L'AMMINISTRATORE
    // ==========================================

    // Inserisce un nuovo piatto nel database (Create)
    public void doSave(Piatto piatto) throws SQLException {
        String query = "INSERT INTO prodotto (nome, descrizione, prezzo, categoria, immagine) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = this.ds.getConnection();
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

    // Modifica un piatto esistente
    public void doUpdate(Piatto piatto) throws SQLException {
        String query = "UPDATE prodotto SET nome = ?, descrizione = ?, prezzo = ?, categoria = ?, immagine = ? WHERE id = ?";
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, piatto.getNome());
            ps.setString(2, piatto.getDescrizione() != null ? piatto.getDescrizione() : "");
            ps.setDouble(3, piatto.getPrezzo());
            ps.setString(4, piatto.getCategoria());
            ps.setString(5, piatto.getImmagine() != null ? piatto.getImmagine() : "default.png");
            ps.setInt(6, piatto.getId());
            ps.executeUpdate();
            System.out.println("PiattoDAO: Piatto con ID " + piatto.getId() + " aggiornato con successo nel DB.");
        }
    }

    // Cancella un piatto
    public void doDeleteLogico(int id) throws SQLException {
        String query = "DELETE FROM prodotto WHERE id = ?"; 
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("PiattoDAO: Piatto con ID " + id + " rimosso dal catalogo.");
        }
    }
}
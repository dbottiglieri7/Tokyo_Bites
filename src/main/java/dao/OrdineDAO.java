package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Carrello;
import model.Piatto;
import model.Ordine;
import utils.ConnessioneDB;

public class OrdineDAO {

    // Salva l'ordine raggruppando i duplicati e inserendo le righe nel DB in modo transazionale
    public boolean salvaOrdineCompleto(Ordine o, String utenteEmail, String indirizzo, String citta, String cap, Carrello carrello) {
        String queryOrdine = "INSERT INTO ordine (utente_email, totale, data_ordine, stato, indirizzo, citta, cap) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryRiga = "INSERT INTO riga_ordine (id_ordine, id_prodotto, quantita, prezzo_acquisto) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psRiga = null;
        
        try {
            conn = ConnessioneDB.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Inserimento della testata dell'ordine
            psOrdine = conn.prepareStatement(queryOrdine, Statement.RETURN_GENERATED_KEYS);
            psOrdine.setString(1, utenteEmail);
            psOrdine.setDouble(2, o.getTotale());
            psOrdine.setTimestamp(3, new java.sql.Timestamp(o.getDataOrdine().getTime()));
            psOrdine.setString(4, o.getStato() != null ? o.getStato() : "In lavorazione");
            psOrdine.setString(5, indirizzo);
            psOrdine.setString(6, citta);
            psOrdine.setString(7, cap);
            
            int affectedRows = psOrdine.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Impossibile creare l'ordine, nessuna riga inserita.");
            }
            
            int idOrdineGenerato = -1;
            try (ResultSet generatedKeys = psOrdine.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idOrdineGenerato = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Impossibile creare l'ordine, ID non ottenuto.");
                }
            }
            
            // 2. RAGGRUPPAMENTO DEI PRODOTTI DUPLICATI
            Map<Integer, Piatto> piattiUnici = new HashMap<>();
            Map<Integer, Integer> quantitaProdotti = new HashMap<>();
            
            for (Piatto piatto : carrello.getElementi()) {
                int idPiatto = piatto.getId();
                piattiUnici.put(idPiatto, piatto);
                quantitaProdotti.put(idPiatto, quantitaProdotti.getOrDefault(idPiatto, 0) + 1);
            }
            
            // 3. Inserimento dei dettagli aggregati in riga_ordine
            psRiga = conn.prepareStatement(queryRiga);
            
            for (Map.Entry<Integer, Integer> entry : quantitaProdotti.entrySet()) {
                int idPiatto = entry.getKey();
                int quantitaTotale = entry.getValue();
                Piatto piatto = piattiUnici.get(idPiatto);
                
                psRiga.setInt(1, idOrdineGenerato);
                psRiga.setInt(2, idPiatto);
                psRiga.setInt(3, quantitaTotale); 
                psRiga.setDouble(4, piatto.getPrezzo()); 
                
                psRiga.addBatch();
            }
            
            psRiga.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Errore nella transazione di salvataggio ordine: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try { if (psRiga != null) psRiga.close(); } catch (SQLException e) {}
            try { if (psOrdine != null) psOrdine.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // NUOVO METODO: Recupera tutti gli ordini di un determinato utente per lo storico ordini
    public List<Ordine> getOrdiniByUtente(String utenteEmail) {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine WHERE utente_email = ? ORDER BY data_ordine DESC";
        
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, utenteEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ordine ord = new Ordine();
                    ord.setId(rs.getInt("id")); 
                    ord.setTotale(rs.getDouble("totale"));
                    ord.setDataOrdine(rs.getTimestamp("data_ordine"));
                    ord.setStato(rs.getString("stato"));
                    ord.setIndirizzo(rs.getString("indirizzo"));
                    ord.setCitta(rs.getString("citta"));
                    ord.setCap(rs.getString("cap"));
                    lista.add(ord);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore in getOrdiniByUtente: " + e.getMessage());
        }
        return lista;
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import model.Carrello;
import model.Piatto;
import model.Ordine;
import utils.ConnessioneDB;

public class OrdineDAO {

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
            // Mappa per associare l'ID del Piatto all'oggetto Piatto stesso (per recuperare il prezzo)
            Map<Integer, Piatto> piattiUnici = new HashMap<>();
            // Mappa per sommare le quantità per ciascun ID prodotto
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
                psRiga.setInt(3, quantitaTotale); // Inserisce la quantità cumulata (es. 3 invece di tre righe da 1)
                psRiga.setDouble(4, piatto.getPrezzo()); // Prezzo unitario di acquisto congelato
                
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
}
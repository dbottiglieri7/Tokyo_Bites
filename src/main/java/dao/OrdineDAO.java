package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Ordine;
import utils.ConnessioneDB;

public class OrdineDAO {

    // Metodo allineato perfettamente al tuo database (8 colonne)
    public boolean salvaOrdine(Ordine o, String indirizzo, String citta, String cap) {
        String query = "INSERT INTO ordine (id_utente, totale, data_ordine, stato, indirizzo, citta, cap) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnessioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, o.getIdUtente());
            ps.setDouble(2, o.getTotale());
            ps.setTimestamp(3, new java.sql.Timestamp(o.getDataOrdine().getTime()));
            ps.setString(4, o.getStato() != null ? o.getStato() : "IN_PREPARAZIONE");
            ps.setString(5, indirizzo);
            ps.setString(6, citta);
            ps.setString(7, cap);
            
            int righeInserite = ps.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Errore in OrdineDAO.salvaOrdine: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        OrdineDAO dao = new OrdineDAO();
        
        // Simuliamo l'ordine (id_utente = 1, totale = 42.50)
        Ordine nuovoOrdine = new Ordine(0, 1, 42.50, new java.util.Date(), "IN_PREPARAZIONE");
        
        System.out.println("--- Test Salvataggio Ordine Reale ---");
        // Passiamo solo i dati richiesti dal tuo DB
        boolean successo = dao.salvaOrdine(nuovoOrdine, "Via Roma 10", "Napoli", "80100");
        
        if (successo) {
            System.out.println(" Ordine salvato con successo su MySQL!");
        } else {
            System.out.println("❌ Errore nel salvataggio dell'ordine.");
        }
    }
}
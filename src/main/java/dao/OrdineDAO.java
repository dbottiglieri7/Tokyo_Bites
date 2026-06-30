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
import javax.sql.DataSource; // Import fondamentale per gestire il Pool di connessioni di Tomcat 11

public class OrdineDAO {

    // Riferimento al DataSource locale del DAO
    private DataSource ds;

    // Costruttore che accetta il DataSource passato dalla Servlet all'atto dell'istanza
    public OrdineDAO(DataSource ds) {
        this.ds = ds;
    }

    // Salva l'ordine raggruppando i duplicati e inserendo le righe nel DB in modo transazionale
    public boolean salvaOrdineCompleto(Ordine o, String utenteEmail, String indirizzo, String citta, String cap, Carrello carrello) {
        String queryOrdine = "INSERT INTO ordine (utente_email, totale, data_ordine, stato, indirizzo, citta, cap) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // CORREZIONE: Usiamo la colonna 'prezzo_acquisto' nativa del DB e la nuova 'nome_archiviato' per congelare il record storico
        String queryRiga = "INSERT INTO riga_ordine (id_ordine, id_prodotto, quantita, prezzo_acquisto, nome_archiviato) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psRiga = null;
        
        try {
            // Otteniamo la connessione dal DataSource locale fornito dal Container
            if (this.ds != null) {
                conn = this.ds.getConnection();
            } else {
                System.err.println("❌ Errore: Il DataSource nel DAO è nullo.");
                return false;
            }
            
            conn.setAutoCommit(false); // Avvia la transazione ACID
            
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
            
            // Recupero dell'ID autogenerato dell'ordine appena creato
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
            
            // 3. Inserimento dei dettagli aggregati in riga_ordine tramite Batch Processing
            psRiga = conn.prepareStatement(queryRiga);
            
            for (Map.Entry<Integer, Integer> entry : quantitaProdotti.entrySet()) {
                int idPiatto = entry.getKey();
                int quantitaTotale = entry.getValue();
                Piatto piatto = piattiUnici.get(idPiatto);
                
                psRiga.setInt(1, idOrdineGenerato);       // id_ordine
                psRiga.setInt(2, idPiatto);               // id_prodotto
                psRiga.setInt(3, quantitaTotale);         // quantita
                psRiga.setDouble(4, piatto.getPrezzo());  // prezzo_acquisto (Nativo)
                psRiga.setString(5, piatto.getNome());    // nome_archiviato (Storico)

                psRiga.addBatch(); // Aggiunge il comando al blocco batch
            }
            
            psRiga.executeBatch(); // Esegue tutti gli inserimenti in un colpo solo
            conn.commit();         // Rende persistenti le modifiche nel DB
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Errore nella transazione di salvataggio ordine: " + e.getMessage());
            if (conn != null) {
                try {
                    System.out.println("🔄 Eseguo il Rollback della transazione...");
                    conn.rollback(); // Annulla tutto in caso di errore per garantire la consistenza
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Chiusura in sicurezza delle risorse JDBC (Le connessioni tornano nel Pool)
            try { if (psRiga != null) psRiga.close(); } catch (SQLException e) {}
            try { if (psOrdine != null) psOrdine.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // Recupera tutti gli ordini di un determinato utente per lo storico ordini
    public List<Ordine> getOrdiniByUtente(String utenteEmail) {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine WHERE utente_email = ? ORDER BY data_ordine DESC";
        
        // Uso del Try-With-Resources ottenendo la connessione dal DataSource locale
        try (Connection conn = (this.ds != null) ? this.ds.getConnection() : null;
             PreparedStatement ps = (conn != null) ? conn.prepareStatement(query) : null) {
            
            if (ps == null) throw new SQLException("Impossibile connettersi al database (DataSource nullo).");
            
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
    
    private String utenteEmail;

    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }

    // ==========================================
    // METODI AGGIUNTI PER L'AMMINISTRATORE
    // ==========================================

    // 1. Recupera tutti gli ordini del sistema
    public List<Ordine> doRetrieveAllOrdini() throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine ORDER BY data_ordine DESC";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ordine ord = mapOrdine(rs);
                lista.add(ord);
            }
        }
        return lista;
    }

    // 2. Filtra gli ordini complessivi da data X a data Y
    public List<Ordine> doRetrieveByDate(String dataInizio, String dataFine) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine WHERE data_ordine BETWEEN ? AND ? ORDER BY data_ordine DESC";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            java.sql.Timestamp inizioTS = java.sql.Timestamp.valueOf(dataInizio + " 00:00:00");
            java.sql.Timestamp fineTS = java.sql.Timestamp.valueOf(dataFine + " 23:59:59");
            
            ps.setTimestamp(1, inizioTS);
            ps.setTimestamp(2, fineTS);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapOrdine(rs));
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Formato data non valido ricevuto nel filtro: " + e.getMessage());
        }
        return lista;
    }

    // 3. Filtra gli ordini complessivi per uno specifico cliente
    public List<Ordine> doRetrieveByCliente(String emailCliente) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine WHERE utente_email = ? ORDER BY data_ordine DESC";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, emailCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapOrdine(rs));
                }
            }
        }
        return lista;
    }
    
    // 4. Filtra gli ordini combinando sia l'intervallo di date che lo specifico cliente
    public List<Ordine> doRetrieveByDateAndCliente(String dataInizio, String dataFine, String emailCliente) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ordine WHERE (data_ordine BETWEEN ? AND ?) AND utente_email = ? ORDER BY data_ordine DESC";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            java.sql.Timestamp inizioTS = java.sql.Timestamp.valueOf(dataInizio + " 00:00:00");
            java.sql.Timestamp fineTS = java.sql.Timestamp.valueOf(dataFine + " 23:59:59");
            
            ps.setTimestamp(1, inizioTS);
            ps.setTimestamp(2, fineTS);
            ps.setString(3, emailCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapOrdine(rs));
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Formato data non valido nel filtro combinato: " + e.getMessage());
        }
        return lista;
    }

    // 5. Modifica lo stato dell'ordine nel database
    public void doUpdateStato(int idOrdine, String nuovoStato) throws SQLException {
        String query = "UPDATE ordine SET stato = ? WHERE id = ?";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuovoStato);
            ps.setInt(2, idOrdine);
            ps.executeUpdate();
        }
    }

    // Mostra i dettagli leggendo i dati congelati
    public List<Piatto> doRetrievePiattiByOrdine(int idOrdine) throws SQLException {
        List<Piatto> listaPiatti = new ArrayList<>();
        String query = "SELECT nome_archiviato, prezzo_acquisto, quantita FROM riga_ordine WHERE id_ordine = ?";
        
        try (Connection conn = this.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idOrdine);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Piatto p = new Piatto();
                    int quantita = rs.getInt("quantita");
                    
                    String nomeProdotto = rs.getString("nome_archiviato");
                    if (nomeProdotto == null) {
                        nomeProdotto = "Prodotto Storico (Catalogo Aggiornato)";
                    }
                    
                    p.setNome(nomeProdotto + " (x" + quantita + ")");
                    p.setPrezzo(rs.getDouble("prezzo_acquisto"));
                    listaPiatti.add(p);
                }
            }
        }
        return listaPiatti;
    }

    // Helper method interno per evitare duplicazione di codice nella mappatura
    private Ordine mapOrdine(ResultSet rs) throws SQLException {
        Ordine ord = new Ordine();
        ord.setId(rs.getInt("id")); 
        ord.setTotale(rs.getDouble("totale"));
        ord.setDataOrdine(rs.getTimestamp("data_ordine"));
        ord.setStato(rs.getString("stato"));
        ord.setIndirizzo(rs.getString("indirizzo"));
        ord.setCitta(rs.getString("citta"));
        ord.setCap(rs.getString("cap"));
        ord.setUtenteEmail(rs.getString("utente_email"));
        
        return ord;
    }
}
package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe Model che rappresenta un ordine effettuato da un utente.
 * Fa parte del layer Model nel pattern architetturale MVC.
 * Mappa la tabella 'ordine' del database.
 *
 * Implementa Serializable per supportare la serializzazione della sessione HTTP
 * nel caso in cui oggetti di questo tipo vengano salvati nella sessione.
 */
public class Ordine implements Serializable {

    private static final long serialVersionUID = 1L;

    // Chiave primaria autogenerata dal database
    private int id;

    // ID numerico dell'utente che ha effettuato l'ordine (FK verso tabella utente)
    private int idUtente;

    // Email dell'utente: usata come riferimento diretto nelle query del DAO
    // (la tabella 'ordine' usa utente_email come chiave di collegamento)
    private String utenteEmail;

    // Totale economico dell'ordine al momento dell'acquisto
    private double totale;

    // Data e ora in cui è stato effettuato l'ordine
    private Date dataOrdine;

    // Stato corrente dell'ordine, gestito dall'amministratore
    // Valori possibili: 'In lavorazione', 'Spedito', 'Consegnato'
    private String stato;

    // Indirizzo di spedizione inserito dall'utente al momento del checkout
    private String indirizzo;
    private String citta;
    private String cap;

    // Costruttore vuoto, necessario per la creazione via setter (es. nella lettura dal ResultSet)
    public Ordine() {}

    // Costruttore completo, utile per istanziare un oggetto già popolato
    public Ordine(int id, int idUtente, double totale, Date dataOrdine, String stato,
                  String indirizzo, String citta, String cap) {
        this.id = id;
        this.idUtente = idUtente;
        this.totale = totale;
        this.dataOrdine = dataOrdine;
        this.stato = stato;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
    }

    // Getter e Setter: incapsulamento degli attributi privati
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }

    public Date getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Date dataOrdine) { this.dataOrdine = dataOrdine; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }
}
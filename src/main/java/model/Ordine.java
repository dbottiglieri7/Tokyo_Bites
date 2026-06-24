package model;

import java.util.Date;

public class Ordine {
    private int id;
    private int idUtente;      // Chi ha fatto l'ordine
    private double totale;     // Prezzo totale pagato
    private Date dataOrdine;   // Quando è stato fatto
    private String stato;      // 'IN_PREPARAZIONE', 'SPEDITO', 'CONSEGNATO'
    private String indirizzo;  // NUOVO
    private String citta;      // NUOVO
    private String cap;        // NUOVO

    // Costruttore vuoto
    public Ordine() {}

    // Costruttore completo aggiornato
    public Ordine(int id, int idUtente, double totale, Date dataOrdine, String stato, String indirizzo, String citta, String cap) {
        this.id = id;
        this.idUtente = idUtente;
        this.totale = totale;
        this.dataOrdine = dataOrdine;
        this.stato = stato;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
    }

    // Getter e Setter esistenti
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }

    public Date getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Date dataOrdine) { this.dataOrdine = dataOrdine; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    // NUOVI Getter e Setter per l'indirizzo di spedizione
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }
}
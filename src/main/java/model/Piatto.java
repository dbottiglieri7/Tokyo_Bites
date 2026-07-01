package model;

import java.io.Serializable;

/**
 * Classe Model che rappresenta un piatto del menu di Tokyo Bites.
 * Fa parte del layer Model nel pattern architetturale MVC.
 * Mappa la tabella 'prodotto' del database.
 *
 * Implementa Serializable per permettere la serializzazione dell'oggetto,
 * necessaria quando viene salvato nella sessione HTTP (es. all'interno
 * dell'oggetto Carrello, che viene mantenuto in sessione).
 */
public class Piatto implements Serializable {

    // Identificatore univoco richiesto da Serializable per garantire
    // la compatibilità durante la deserializzazione
    private static final long serialVersionUID = 1L;

    // Chiave primaria autogenerata dal database
    private int id;

    // Nome del piatto (es. "Nigiri Salmone")
    private String nome;

    // Descrizione testuale del piatto mostrata nella pagina menu
    private String descrizione;

    // Prezzo attuale del piatto nel catalogo.
    // NOTA: il prezzo al momento dell'acquisto viene salvato separatamente
    // nella colonna 'prezzo_acquisto' di 'riga_ordine', garantendo
    // l'integrità dello storico ordini anche se il prezzo viene modificato.
    private double prezzo;

    // Categoria del piatto, usata per il filtraggio nel menu (es. "Sushi e Sashimi")
    private String categoria;

    // Nome del file immagine del piatto relativo alla cartella /images del progetto
    private String immagine;

    // Costruttore vuoto, necessario per la creazione via setter (es. nella lettura dal ResultSet)
    public Piatto() {}

    // Costruttore completo, utile per istanziare un oggetto già popolato
    public Piatto(int id, String nome, String descrizione, double prezzo, String categoria, String immagine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.immagine = immagine;
    }

    // Getter e Setter: incapsulamento degli attributi privati
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }
}
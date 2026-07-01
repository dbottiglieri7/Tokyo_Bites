package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Model che rappresenta il carrello della spesa dell'utente.
 * Fa parte del layer Model nel pattern architetturale MVC.
 *
 * Il carrello NON viene salvato nel database: viene mantenuto interamente
 * nella sessione HTTP dell'utente (HttpSession), come richiesto dalla checklist.
 * Al completamento dell'acquisto, il contenuto viene trasferito nel database
 * tramite OrdineDAO.salvaOrdineCompleto() e il carrello viene svuotato.
 *
 * Implementa Serializable perché viene salvato nella sessione HTTP:
 * Tomcat può serializzare la sessione su disco (es. al riavvio), quindi
 * tutti gli oggetti in sessione devono essere serializzabili.
 * Anche Piatto implementa Serializable per lo stesso motivo.
 */
public class Carrello implements Serializable {

    private static final long serialVersionUID = 1L;

    // Lista dei piatti aggiunti dall'utente. Può contenere duplicati:
    // lo stesso piatto aggiunto due volte appare due volte nella lista.
    // Il raggruppamento per quantità avviene in OrdineDAO al momento del salvataggio.
    private List<Piatto> elementi;

    // Costruttore: inizializza la lista vuota alla creazione del carrello
    public Carrello() {
        this.elementi = new ArrayList<>();
    }

    /**
     * Aggiunge un piatto alla lista. Se lo stesso piatto viene aggiunto
     * più volte, appare più volte nella lista (gestione a "pezzi singoli").
     */
    public void aggiungiPiatto(Piatto p) {
        if (p != null) {
            elementi.add(p);
        }
    }

    /**
     * Rimuove UNA SOLA occorrenza del piatto con l'ID specificato.
     * Se lo stesso piatto è presente più volte, ne rimuove solo uno,
     * simulando la riduzione di quantità di 1 unità.
     */
    public void rimuoviPiatto(int idPiatto) {
        for (int i = 0; i < elementi.size(); i++) {
            if (elementi.get(i).getId() == idPiatto) {
                elementi.remove(i); // Rimuove solo la prima occorrenza trovata
                break;
            }
        }
    }

    /**
     * Restituisce la lista completa dei piatti nel carrello (con eventuali duplicati).
     * Usato da OrdineDAO per costruire le righe dell'ordine.
     */
    public List<Piatto> getElementi() {
        return elementi;
    }

    /**
     * Calcola il totale sommando il prezzo di ogni elemento nella lista.
     * Poiché i duplicati sono rappresentati come elementi separati,
     * la somma è automaticamente corretta anche con più unità dello stesso piatto.
     */
    public double getPrezzoTotale() {
        double totale = 0;
        for (Piatto p : elementi) {
            totale += p.getPrezzo();
        }
        return totale;
    }

    /**
     * Svuota completamente il carrello.
     * Viene chiamato dalla servlet di conferma ordine dopo il salvataggio nel DB.
     */
    public void svuota() {
        elementi.clear();
    }

    /**
     * Restituisce il numero totale di elementi nel carrello (contando i duplicati).
     * Usato tipicamente per mostrare il badge con il numero di articoli nell'icona carrello.
     */
    public int getQuantitaTotale() {
        return elementi.size();
    }
}
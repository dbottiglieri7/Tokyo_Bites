package model;

import java.util.ArrayList;
import java.util.List;

public class Carrello {
    private List<Piatto> elementi;

    public Carrello() {
        this.elementi = new ArrayList<>();
    }

    // Aggiunge un piatto al carrello
    public void aggiungiPiatto(Piatto p) {
        if (p != null) {
            elementi.add(p);
        }
    }

 // Rimuove SOLO UN PEZZO di quel piatto dal carrello tramite ID
    public void rimuoviPiatto(int idPiatto) {
        for (int i = 0; i < elementi.size(); i++) {
            if (elementi.get(i).getId() == idPiatto) {
                elementi.remove(i); // Rimuove solo questo elemento e interrompe il ciclo
                break; 
            }
        }
    }
    // Ritorna la lista dei piatti nel carrello
    public List<Piatto> getElementi() {
        return elementi;
    }

    // Calcola il prezzo totale del carrello
    public double getPrezzoTotale() {
        double totale = 0;
        for (Piatto p : elementi) {
            totale += p.getPrezzo();
        }
        return totale;
    }

    // Svuota completamente il carrello
    public void svuota() {
        elementi.clear();
    }
    
    // Conta quanti elementi ci sono nel carrello
    public int getQuantitaTotale() {
        return elementi.size();
    }
}
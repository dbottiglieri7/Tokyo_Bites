package model;

import java.io.Serializable;

/**
 * Classe Model che rappresenta un utente del sistema Tokyo Bites.
 * Fa parte del layer Model nel pattern architetturale MVC.
 * Mappa la tabella 'utente' del database.
 *
 * Implementa Serializable perché l'oggetto Utente viene salvato nella sessione
 * HTTP dopo il login (tramite session.setAttribute), e Tomcat richiede che
 * tutti gli oggetti in sessione siano serializzabili.
 */
public class Utente implements Serializable {

    private static final long serialVersionUID = 1L;

    // Chiave primaria autogenerata dal database
    private int id;

    // Email univoca dell'utente, usata come identificatore di login
    private String email;

    // Password dell'utente
    private String password;

    // Nome e cognome dell'utente
    private String nome;
    private String cognome;

    // Ruolo dell'utente nel sistema: 'CLIENTE' per utenti normali, 'ADMIN' per amministratori
    // Usato per il controllo degli accessi nelle Servlet tramite token di sessione
    private String ruolo;

    // Costruttore vuoto, necessario per la creazione dell'oggetto prima del popolamento via setter
    public Utente() {}

    // Costruttore completo, utile per creare un oggetto già popolato in una sola riga
    public Utente(int id, String email, String password, String nome, String cognome, String ruolo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
    }

    // Getter e Setter: incapsulamento degli attributi privati
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}
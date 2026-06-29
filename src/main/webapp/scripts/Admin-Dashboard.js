/**
 * Questa funzione serve quando l'amministratore clicca sul tasto "Modifica" di un piatto.
 * Invece di far cambiare pagina, prende i dati del piatto e li scrive dentro i campi del form in alto.
 */
function preparaModifica(id, nome, prezzo, categoria, descrizione, immagine) {
    // Cambia il titolo del form per far capire che stiamo modificando
    document.getElementById('form-title').innerText = "Modifica Prodotto (ID: " + id + ")";
    
    // Dice alla Servlet che quando invieremo il form dovremo fare una "modifica" e non un nuovo inserimento
    document.getElementById('form-azione').value = "modifica";
    document.getElementById('form-id').value = id;
    
    // Inserisce i dati attuali del piatto nei quadratini di testo (input)
    document.getElementById('nome').value = nome;     				// Popola il campo nome
    document.getElementById('prezzo').value = prezzo; 				// Popola il campo prezzo
    document.getElementById('categoria').value = categoria; 		// Popola il campo categoria
    document.getElementById('descrizione').value = descrizione; 	// Popola il campo descrizione
    document.getElementById('immagine').value = immagine; 			// Popola il campo immagine
    
    // Trasforma il bottone d'invio in un tasto giallo con scritto "Salva Modifiche"
    document.getElementById('btn-submit').innerText = "Salva Modifiche";
    document.getElementById('btn-submit').style.background = "#ffcc00";
    document.getElementById('btn-submit').style.color = "#000";
    
    // Fa comparire il tasto "Annulla Modifica" (che prima era nascosto)
    document.getElementById('btn-annulla').style.display = "inline-block";
    
    // Fa risalire la pagina in alto in modo fluido per mostrare il form compilato
    window.scrollTo({top: 0, behavior: 'smooth'});
}

/**
 * Questa funzione svuota il form e lo fa tornare come prima.
 * Serve se l'utente ci ripensa e vuole inserire un nuovo piatto invece di modificarne uno esistente.
 */
function resettaForm() {
    // Rimette il titolo originale
    document.getElementById('form-title').innerText = "Aggiungi Nuovo Prodotto nel Catalogo";
    
    // Dice alla Servlet che la prossima operazione sarà di nuovo un normale "inserisci"
    document.getElementById('form-azione').value = "inserisci";
    document.getElementById('form-id').value = "";
    
    // Svuota tutti i campi di testo del form
    document.getElementById('nome').value = "";
    document.getElementById('prezzo').value = "";
    document.getElementById('categoria').selectedIndex = 0; // Rimette la prima opzione della lista
    document.getElementById('descrizione').value = "";
    document.getElementById('immagine').value = ""; // <-- AGGIUNTO: Svuota il campo immagine
    
    // Rimette il bottone d'invio allo stato iniziale (colore di default e testo originale)
    document.getElementById('btn-submit').innerText = "Inserisci Piatto";
    document.getElementById('btn-submit').style.background = ""; 
    document.getElementById('btn-submit').style.color = "";
    
    // Nasconde di nuovo il tasto "Annulla Modifica"
    document.getElementById('btn-annulla').style.display = "none";
}
/**
 * Gestisce l'aggiornamento asincrono (AJAX) del carrello.
 * Invia al server l'ID del piatto e l'azione da compiere (es. aggiungi, rimuovi, svuota).
 * Riceve dal server un oggetto JSON con i dati aggiornati e modifica il DOM della pagina al volo.
 */
function gestisciCarrelloAjax(idPiatto, azioneUtente, contextPath) {
    // 1. Prepara i parametri da inviare alla Servlet in formato chiave=valore
    const urlParams = new URLSearchParams();
    urlParams.append('idPiatto', idPiatto);
    urlParams.append('azione', azioneUtente);

    // 2. Esegue la richiesta asincrona (POST) verso la Servlet "/Carrello"
    fetch(contextPath + '/Carrello', { 
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest' // Segnala alla servlet che si tratta di una richiesta AJAX
        },
        body: urlParams.toString()
    })
    .then(response => response.json()) // Converte la risposta del server in un oggetto JSON utilizzabile
    .then(data => {
        if (data.success) {
            
            // --- GESTIONE DINAMICA DEL CONTATORE DELLA NAVBAR ---
            // Troviamo lo span rosso del carrello nella barra in alto
            const contatoreNavbar = document.querySelector(".nav-links a[href*='Carrello'] span");
            
            // CASO A: Il carrello è vuoto (o è stato svuotato del tutto)
            if (data.carrelloVuoto) {
                if (contatoreNavbar) contatoreNavbar.innerText = "0"; // Azzera la navbar
                
                document.getElementById('carrello-wrapper').innerHTML = `
                    <h1 class="menu-title" style="color: yellow !important;">Il Tuo Carrello 🛒</h1>
                    <div style="text-align: center; padding: 40px; color: #aaa;">
                        <p style="font-size: 1.3rem; margin-bottom: 20px;">Il tuo carrello è attualmente vuoto.</p>
                        <a href="${contextPath}/Menu" class="tab active" style="text-decoration: none;">Torna al Menu</a>
                    </div>
                `;
            } else {
                // Se abbiamo rimosso un singolo piatto, scaliamo di 1 il numerino in alto
                if (contatoreNavbar && azioneUtente === 'rimuovi') {
                    let numeroAttuale = parseInt(contatoreNavbar.innerText, 10);
                    if (!isNaN(numeroAttuale) && numeroAttuale > 0) {
                        contatoreNavbar.innerText = numeroAttuale - 1;
                    }
                }

                // CASO B: È stata solo ridotta la quantità di un piatto
                if (data.nuovaQuantita > 0) {
                    // Aggiorna la quantità (es: x2) e il prezzo parziale di quella riga
                    document.getElementById('qty-' + data.idPiatto).innerText = 'x' + data.nuovaQuantita;
                    document.getElementById('prezzo-' + data.idPiatto).innerText = '€ ' + data.prezzoParziale;
                } else {
                    // CASO C: La quantità è scesa a 0 (il piatto è stato rimosso del tutto)
                    const riga = document.getElementById('riga-piatto-' + data.idPiatto);
                    if (riga) riga.remove();
                }
                
                // Aggiorna la scritta del prezzo Totale in fondo alla pagina (usando la tua variabile originale)
                document.getElementById('totale-carrello').innerText = data.prezzoTotale;
            }
        }
    })
    .catch(error => console.error('Errore nell\'elaborazione AJAX del carrello:', error));
}
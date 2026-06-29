// Funzione asincrona per aggiornare lo stato dell'ordine (es. da "In lavorazione" a "Consegnato") senza ricaricare la pagina
function modificaStato(select) {
    var form = select.form;
    
    // Prepara i dati da spedire alla servlet in formato chiave=valore
    var dati = "azione=" + encodeURIComponent(form.azione.value) +
               "&idOrdine=" + encodeURIComponent(form.idOrdine.value) +
               "&nuovoStato=" + encodeURIComponent(select.value);
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", form.action, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                console.log("Stato aggiornato con successo.");
            } else {
                alert("Errore durante l'aggiornamento dello stato.");
            }
        }
    };
    
    xhr.send(dati);
}

/**
 * Funzione per recuperare i piatti di un ordine via AJAX e mostrarli nel Pop-up (Modal)
 * NOTA: Riceve anche il 'contextPath' passato dalla JSP per evitare l'errore del percorso vuoto
 */
function mostraDettagli(event, idOrdine, contextPath) {
    if (event) event.preventDefault(); // Blocca l'eventuale comportamento di default del bottone

    // Scrive il numero dell'ordine nel titolo del pop-up (es. #42)
    document.getElementById("modalIdOrdine").innerText = "#" + idOrdine;
    
    var lista = document.getElementById("listaPiatti");
    lista.innerHTML = "<li>Caricamento in corso...</li>";
    
    // Fa comparire la finestrella pop-up modificando lo stile CSS
    document.getElementById("dettagliModal").style.display = "block";
    
    // Effettua la chiamata GET asincrona usando il percorso corretto passato come parametro
    var xhr = new XMLHttpRequest();
    xhr.open("GET", contextPath + "/AdminDashboard?azione=dettaglioOrdine&idOrdine=" + idOrdine, true);
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    // Trasforma la stringa JSON ricevuta dalla Servlet in un array JavaScript di piatti
                    var piatti = JSON.parse(xhr.responseText);
                    lista.innerHTML = ""; // Svuota la scritta "Caricamento in corso..."
                    
                    if (piatti.length === 0) {
                        lista.innerHTML = "<li>Nessun dettaglio trovato per questo ordine.</li>";
                    } else {
                        // Cicla tutti i piatti dell'ordine e crea un elemento della lista <li> per ognuno
                        piatti.forEach(function(piatto) {
                            var li = document.createElement("li");
                            li.innerHTML = "🍣 <strong>" + piatto.nome + "</strong> - <span style='color: #ff3838;'>" + Number(piatto.prezzo).toFixed(2) + " €</span>";
                            lista.appendChild(li);
                        });
                    }
                } catch (e) {
                    lista.innerHTML = "<li style='color:red;'>Errore nella lettura dei dati (JSON non valido).</li>";
                }
            } else {
                lista.innerHTML = "<li style='color:red;'>Errore server: Stato " + xhr.status + "</li>";
            }
        }
    };
    xhr.send();
}

// Funzione per nascondere il pop-up quando si clicca sulla "X"
function chiudiModal() {
    document.getElementById("dettagliModal").style.display = "none";
}

// Chiude il pop-up in automatico se l'utente clicca fuori dalla finestrella nera
window.onclick = function(event) {
    var modal = document.getElementById("dettagliModal");
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
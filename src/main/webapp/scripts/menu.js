// Filtro di ricerca istantaneo che si attiva a ogni lettera digitata
function filtraPiattiInTempoReale() {
    const barraRicerca = document.getElementById('menu-search');
    const filtro = barraRicerca.value.toLowerCase().trim(); // Prende il testo cercato, in minuscolo e senza spazi vuoti
    const schedePiatti = document.getElementsByClassName('product-card-item'); // Prende tutti i piatti della pagina

    // Ciclo che controlla ogni singolo piatto per vedere se corrisponde alla ricerca
    for (let i = 0; i < schedePiatti.length; i++) {
        const tagNome = schedePiatti[i].querySelector('.target-name');
        if (tagNome) {
            // Facciamo una copia temporanea del tag del nome per poter togliere il moltiplicatore (es. x2)
            // in modo che se l'utente cerca "Sushi" non venga confuso dal testo "(x2)"
            const copiaNodo = tagNome.cloneNode(true);
            const spanInterno = copiaNodo.querySelector('span');
            if (spanInterno) {
                copiaNodo.removeChild(spanInterno); // Rimuove temporaneamente il quadratino della quantità
            }
            
            const testoNomePulito = copiaNodo.textContent.toLowerCase().trim();
            
            // Se il nome del piatto contiene la parola cercata lo lascia visibile, altrimenti lo nasconde
            if (testoNomePulito.includes(filtro)) {
                schedePiatti[i].style.display = "";
            } else {
                schedePiatti[i].style.display = "none";
            }
        }
    }
}

// Funzione per aprire il pop-up (finestra modale) dell'immagine ingrandita
function apriIngrandimento(srcImmagine, nomePiatto) {
    const modal = document.getElementById('image-zoom-modal');
    const modalImg = document.getElementById('img-target-zoom');
    const caption = document.getElementById('modal-caption');
    
    modal.style.display = "block"; // Mostra il box del pop-up
    modalImg.src = srcImmagine; // Imposta la foto da mostrare
    caption.innerHTML = nomePiatto; // Mette il nome del piatto sotto la foto
}

// Funzione per chiudere il pop-up dell'immagine ingrandita quando si clicca sullo sfondo
function chiudiIngrandimento() {
    document.getElementById('image-zoom-modal').style.display = "none";
}

// Chiamata asincrona (AJAX) per aggiungere un piatto al carrello senza far ricaricare la pagina
function aggiungiAlCarrelloAjax(event, formElement, idPiatto) {
    event.preventDefault(); // Blocca l'invio classico del form che farebbe ricaricare la pagina
    const formData = new URLSearchParams(new FormData(formElement)); // Prepara i dati del form da inviare

    // Invia i dati alla Servlet del Carrello in background
    fetch(formElement.action, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest' // Dice alla Servlet che si tratta di una richiesta asincrona AJAX
        },
        body: formData.toString()
    })
    .then(response => response.json()) // Riceve la risposta dalla Servlet in formato JSON
    .then(data => {
        if (data.success) {
            // Se l'aggiunta ha avuto successo, aumenta di 1 il contatore rosso del carrello in alto nella barra
            const badge = document.getElementById('badge-carrello');
            let conteggioAttuale = parseInt(badge.innerText, 10);
            badge.innerText = conteggioAttuale + 1;

            // Aggiorna o mostra la scritta gialla vicino al piatto (es. da x1 diventa x2)
            const moltiplicatoreSpan = document.getElementById('moltiplicatore-' + idPiatto);
            if (moltiplicatoreSpan) {
                moltiplicatoreSpan.innerText = '(x' + data.nuovaQuantita + ')';
                moltiplicatoreSpan.style.display = 'inline-block';
            }
        }
    })
    .catch(error => console.error('Errore nell\'aggiunta AJAX dal menu:', error));
}

// Salva la posizione dello scroll prima che la pagina si chiuda o cambi categoria
window.addEventListener('beforeunload', function() {
    localStorage.setItem('scrollPositionMenu', window.scrollY);
});

// Ripristina la vecchia posizione dello scroll non appena la nuova pagina ha finito di caricarsi
// Evita che la pagina torni bruscamente in alto se l'utente cambia ad esempio categoria
window.addEventListener('DOMContentLoaded', function() {
    const savedPosition = localStorage.getItem('scrollPositionMenu');
    if (savedPosition) {
        window.scrollTo(0, parseInt(savedPosition, 10)); // Rimette lo scroll dove si trovava prima
        localStorage.removeItem('scrollPositionMenu'); // Cancella il dato salvato per non usarlo a sproposito la volta dopo
    }
});
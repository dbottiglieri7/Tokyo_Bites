/**
 * validazioneAdmin.js
 * Validazione lato client per i form amministratore (Inserimento / Modifica Piatti)
 * Rispetta i requisiti: Espressioni regolari, evento change, evento submit, manipolazione DOM (NO alert)
 */

document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("formProdotto");
    if (!form) return;

    const nomeInput = document.getElementById("nome");
    const prezzoInput = document.getElementById("prezzo");

    // Regex per la validazione
    // Nome: Lettere, numeri, spazi e caratteri tipici dei piatti (tra 2 e 50 caratteri)
    const nomeRegex = /^[A-Za-z0-9 🍣🍱🍜🥟🍤🥠'\-]{2,50}$/;
    // Prezzo: Numero decimale positivo (es: 10, 10.5, 9.99)
    const prezzoRegex = /^[0-9]+(\.[0-9]{1,2})?$/;

    // Funzione generica per mostrare l'errore modificando il DOM
    function mostraErrore(inputElement, messaggio) {
        let erroreSpan = inputElement.nextElementSibling;
        
        // Se non esiste già lo span di errore, lo creiamo
        if (!erroreSpan || !erroreSpan.classList.contains("errore-messaggio")) {
            erroreSpan = document.createElement("span");
            erroreSpan.classList.add("errore-messaggio");
            erroreSpan.style.color = "#ff3838";
            erroreSpan.style.fontSize = "0.9pt";
            erroreSpan.style.display = "block";
            erroreSpan.style.marginTop = "5px";
            inputElement.parentNode.insertBefore(erroreSpan, inputElement.nextSibling);
        }
        
        erroreSpan.textContent = messaggio;
        inputElement.style.border = "2px solid #ff3838";
    }

    // Funzione generica per rimuovere l'errore dal DOM
    function rimuoviErrore(inputElement) {
        let erroreSpan = inputElement.nextElementSibling;
        if (erroreSpan && erroreSpan.classList.contains("errore-messaggio")) {
            erroreSpan.remove();
        }
        inputElement.style.border = "1px solid #ccc";
    }

    // Validazione del campo Nome
    function validaNome() {
        if (!nomeRegex.test(nomeInput.value.trim())) {
            mostraErrore(nomeInput, "Il nome deve essere compreso tra 2 e 50 caratteri (lettere, numeri o emoticons consentite).");
            return false;
        }
        rimuoviErrore(nomeInput);
        return true;
    }

    // Validazione del campo Prezzo
    function validaPrezzo() {
        if (!prezzoRegex.test(prezzoInput.value.trim()) || parseFloat(prezzoInput.value) <= 0) {
            mostraErrore(prezzoInput, "Inserisci un prezzo valido e maggiore di 0 (es. 12.50).");
            return false;
        }
        rimuoviErrore(prezzoInput);
        return true;
    }

    // 1. Associazione eventi 'change' per la validazione immediata al termine dell'inserimento
    nomeInput.addEventListener("change", validaNome);
    prezzoInput.addEventListener("change", validaPrezzo);

    // 2. Associazione evento 'submit' per il blocco dell'invio se ci sono errori
    form.addEventListener("submit", function(event) {
        const isNomeValido = validaNome();
        const isPrezzoValido = validaPrezzo();

        // Se uno dei campi non è valido, blocchiamo l'invio al server
        if (!isNomeValido || !isPrezzoValido) {
            event.preventDefault();
            console.log("Invio bloccato: validazione fallita.");
        }
    });
});
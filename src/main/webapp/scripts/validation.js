// Aspetta che tutta la pagina HTML sia caricata prima di attivare i controlli JavaScript
document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("checkoutForm");

    // --- Espressioni Regolari (Regex) per controllare il formato dei testi ---
    const regexCap = /^[0-8]\d{4}$/; // Controlla che siano esattamente 5 numeri
    const regexCarta = /^\d{16}$/;  // Controlla che siano esattamente 16 numeri
    const regexCVV = /^\d{3}$/;     // Controlla che siano esattamente 3 numeri
    const regexScadenza = /^(0[1-9]|1[0-2])\/\d{4}$/; // Controlla il formato Mese/Anno a 4 cifre (es: 12/2028)

    /**
     * Funzione tuttofare per convalidare un singolo quadratino di input.
     * Se la condizione è FALSA, colora il bordo di rosso e stampa l'errore sotto l'input.
     * Se la condizione è VERA, pulisce l'errore e rimette il bordo normale.
     */
    function validaCampo(input, condizione, messaggio, erroreEl) {
        if (!condizione) {
            erroreEl.textContent = messaggio; // Scrive la frase di errore nello span
            input.style.borderColor = "#ff3838"; // Bordo rosso di errore
            return false;
        } else {
            erroreEl.textContent = ""; // Cancella la frase di errore
            input.style.borderColor = "#333"; // Torna al bordo grigio originale
            return true;
        }
    }

    // --- CONTROLLI IN TEMPO REALE SULL'INPUT (Bloccano i caratteri sbagliati mentre digiti) ---

    // 1. Intestatario: cancella immediatamente qualsiasi numero o simbolo speciale
    form.intestatario.addEventListener("input", function() {
        this.value = this.value.replace(/[^a-zA-Z\sàèìòùÀÈÌÒÙáéíóúÁÉÍÓÚ]/g, "");
    });

	// 2. Città: cancella immediatamente qualsiasi numero o simbolo speciale
    form.citta.addEventListener("input", function() {
        this.value = this.value.replace(/[^a-zA-Z\sàèìòùÀÈÌÒÙáéíóúÁÉÍÓÚ]/g, "");
    });
		
    // 3. Numero Carta: cancella all'istante qualsiasi lettera o spazio inserito
    form.numeroCarta.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, ""); // (\D significa "tutto ciò che NON è un numero")
    });

    // 4. CVV: cancella all'istante qualsiasi lettera o spazio inserito
    form.cvv.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, "");
    });

    // 5. CAP: cancella all'istante qualsiasi lettera o spazio inserito
    form.cap.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, "");
    });

    // 6. Scadenza: cancella le lettere e mette da solo il simbolo "/" dopo le prime due cifre (il mese)
    form.scadenza.addEventListener("input", function(e) {
        let input = this.value.replace(/\D/g, ""); // Tira via le lettere
        
        if (input.length > 2) {
            // Prende i primi due numeri, ci incolla lo slash "/" e poi mette gli altri numeri dell'anno
            this.value = input.substring(0, 2) + "/" + input.substring(2, 6);
        } else {
            this.value = input;
        }
    });


    // --- CONTROLLI ALL'USCITA DAI CAMPI (Si attivano quando l'utente passa al quadratino successivo) ---
    form.indirizzo.addEventListener("change", () => validaCampo(form.indirizzo, form.indirizzo.value.trim().length > 4, "L'indirizzo deve contenere almeno 5 caratteri", document.getElementById("error-indirizzo")));
    form.citta.addEventListener("change", () => validaCampo(form.citta, form.citta.value.trim().length > 2, "Inserisci una città valida", document.getElementById("error-citta")));
    form.cap.addEventListener("change", () => validaCampo(form.cap, regexCap.test(form.cap.value), "Il CAP deve essere di 5 cifre numeriche", document.getElementById("error-cap")));
    form.intestatario.addEventListener("change", () => validaCampo(form.intestatario, form.intestatario.value.trim().length > 4, "Inserisci il nome completo dell'intestatario", document.getElementById("error-intestatario")));
    form.numeroCarta.addEventListener("change", () => validaCampo(form.numeroCarta, regexCarta.test(form.numeroCarta.value), "Il numero della carta deve contenere 16 cifre", document.getElementById("error-numeroCarta")));
    form.scadenza.addEventListener("change", () => validaCampo(form.scadenza, regexScadenza.test(form.scadenza.value), "Usa il formato MM/AAAA (es. 12/2028)", document.getElementById("error-scadenza")));
    form.cvv.addEventListener("change", () => validaCampo(form.cvv, regexCVV.test(form.cvv.value), "Il CVV deve essere di 3 cifre", document.getElementById("error-cvv")));


    // --- VALIDAZIONE FINALE AL SUBMIT (Scatta quando l'utente clicca sul pulsante per pagare) ---
    form.addEventListener("submit", function(event) {
        let isValido = true; // Di base supponiamo che tutto sia corretto

        // Esegue il controllo su tutti i campi uno per uno. Se anche uno solo fallisce, mette la variabile su false.
        if (!validaCampo(form.indirizzo, form.indirizzo.value.trim().length > 4, "L'indirizzo deve contenere almeno 5 caratteri", document.getElementById("error-indirizzo"))) isValido = false;
        if (!validaCampo(form.citta, form.citta.value.trim().length > 2, "Inserisci una città valida", document.getElementById("error-citta"))) isValido = false;
        if (!validaCampo(form.cap, regexCap.test(form.cap.value), "Il CAP deve essere di 5 cifre numeriche", document.getElementById("error-cap"))) isValido = false;
        if (!validaCampo(form.intestatario, form.intestatario.value.trim().length > 4, "Inserisci il nome completo dell'intestatario", document.getElementById("error-intestatario"))) isValido = false;
        if (!validaCampo(form.numeroCarta, regexCarta.test(form.numeroCarta.value), "Il numero della carta deve contenere 16 cifre", document.getElementById("error-numeroCarta"))) isValido = false;
        if (!validaCampo(form.scadenza, regexScadenza.test(form.scadenza.value), "Usa il formato MM/AAAA (es. 12/2028)", document.getElementById("error-scadenza"))) isValido = false;
        if (!validaCampo(form.cvv, regexCVV.test(form.cvv.value), "Il CVV deve essere di 3 cifre", document.getElementById("error-cvv"))) isValido = false;

        // Se un campo è sbagliato, blocca la spedizione dei dati al server Java (impedisce la sottomissione del form)
        if (!isValido) {
            event.preventDefault();
        }
    });
});
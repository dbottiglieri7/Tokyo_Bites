document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("checkoutForm");

    // Espressioni Regolari (Regex) richieste dalla traccia
    const regexCap = /^[0-8]\d{4}$/; // 5 cifre numeriche
    const regexCarta = /^\d{16}$/;  // 16 cifre numeriche
    const regexCVV = /^\d{3}$/;     // 3 cifre numeriche
    const regexScadenza = /^(0[1-9]|1[0-2])\/\d{4}$/; // MM/AAAA

    // Funzione per mostrare o nascondere l'errore modificando il DOM
    function validaCampo(input, condizione, messaggio, erroreEl) {
        if (!condizione) {
            erroreEl.textContent = messaggio;
            input.style.borderColor = "#ff3838";
            return false;
        } else {
            erroreEl.textContent = "";
            input.style.borderColor = "#333";
            return true;
        }
    }

    // --- CONTROLLI IN TEMPO REALE SULL'INPUT (Bloccano i caratteri errati) ---

    // 1. Intestatario: accetta SOLO lettere e spazi (rimuove numeri e simboli)
    form.intestatario.addEventListener("input", function() {
        this.value = this.value.replace(/[^a-zA-Z\sàèìòùÀÈÌÒÙáéíóúÁÉÍÓÚ]/g, "");
    });

	// 2. città: accetta SOLO lettere e spazi (rimuove numeri e simboli)
	    form.citta.addEventListener("input", function() {
	        this.value = this.value.replace(/[^a-zA-Z\sàèìòùÀÈÌÒÙáéíóúÁÉÍÓÚ]/g, "");
	 });
		
    // 3. Numero Carta: accetta solo numeri
    form.numeroCarta.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, ""); // Rimuove tutto ciò che non è un numero
    });

    // 4. CVV: accetta solo numeri
    form.cvv.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, ""); // Rimuove tutto ciò che non è un numero
    });

    // 5. CAP: accetta solo numeri
    form.cap.addEventListener("input", function() {
        this.value = this.value.replace(/\D/g, ""); // Rimuove tutto ciò che non è un numero
    });

    // 6. Scadenza: accetta numeri e mette lo slash / in automatico dopo il mese
    form.scadenza.addEventListener("input", function(e) {
        let input = this.value.replace(/\D/g, ""); // Rimuove i caratteri non numerici
        
        if (input.length > 2) {
            // Separa il mese dall'anno inserendo lo slash in mezzo
            this.value = input.substring(0, 2) + "/" + input.substring(2, 6);
        } else {
            this.value = input;
        }
    });


    // --- GESTIONE EVENTO 'CHANGE' (Validazione all'uscita dal campo) ---
    form.indirizzo.addEventListener("change", () => validaCampo(form.indirizzo, form.indirizzo.value.trim().length > 4, "L'indirizzo deve contenere almeno 5 caratteri", document.getElementById("error-indirizzo")));
    form.citta.addEventListener("change", () => validaCampo(form.citta, form.citta.value.trim().length > 2, "Inserisci una città valida", document.getElementById("error-citta")));
    form.cap.addEventListener("change", () => validaCampo(form.cap, regexCap.test(form.cap.value), "Il CAP deve essere di 5 cifre numeriche", document.getElementById("error-cap")));
    form.intestatario.addEventListener("change", () => validaCampo(form.intestatario, form.intestatario.value.trim().length > 4, "Inserisci il nome completo dell'intestatario", document.getElementById("error-intestatario")));
    form.numeroCarta.addEventListener("change", () => validaCampo(form.numeroCarta, regexCarta.test(form.numeroCarta.value), "Il numero della carta deve contenere 16 cifre", document.getElementById("error-numeroCarta")));
    form.scadenza.addEventListener("change", () => validaCampo(form.scadenza, regexScadenza.test(form.scadenza.value), "Usa il formato MM/AAAA (es. 12/2028)", document.getElementById("error-scadenza")));
    form.cvv.addEventListener("change", () => validaCampo(form.cvv, regexCVV.test(form.cvv.value), "Il CVV deve essere di 3 cifre", document.getElementById("error-cvv")));


    // --- GESTIONE EVENTO 'SUBMIT' (Validazione finale prima dell'invio) ---
    form.addEventListener("submit", function(event) {
        let isValido = true;

        if (!validaCampo(form.indirizzo, form.indirizzo.value.trim().length > 4, "L'indirizzo deve contenere almeno 5 caratteri", document.getElementById("error-indirizzo"))) isValido = false;
        if (!validaCampo(form.citta, form.citta.value.trim().length > 2, "Inserisci una città valida", document.getElementById("error-citta"))) isValido = false;
        if (!validaCampo(form.cap, regexCap.test(form.cap.value), "Il CAP deve essere di 5 cifre numeriche", document.getElementById("error-cap"))) isValido = false;
        if (!validaCampo(form.intestatario, form.intestatario.value.trim().length > 4, "Inserisci il nome completo dell'intestatario", document.getElementById("error-intestatario"))) isValido = false;
        if (!validaCampo(form.numeroCarta, regexCarta.test(form.numeroCarta.value), "Il numero della carta deve contenere 16 cifre", document.getElementById("error-numeroCarta"))) isValido = false;
        if (!validaCampo(form.scadenza, regexScadenza.test(form.scadenza.value), "Usa il formato MM/AAAA (es. 12/2028)", document.getElementById("error-scadenza"))) isValido = false;
        if (!validaCampo(form.cvv, regexCVV.test(form.cvv.value), "Il CVV deve essere di 3 cifre", document.getElementById("error-cvv"))) isValido = false;

        // Se anche un solo campo è errato, blocchiamo l'invio al server
        if (!isValido) {
            event.preventDefault();
        }
    });
});
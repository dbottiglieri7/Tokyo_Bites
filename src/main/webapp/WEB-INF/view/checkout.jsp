<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tokyo Bites - Checkout</title>
    <%-- Collegamento al file CSS principale per mantenere lo stile scuro e coerente --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page">

    <div class="menu-container" style="max-width: 600px; margin-top: 50px;">
        
        <% 
            // Se l'ordine è stato completato con successo, mostra la schermata di conferma senza il form
            if (request.getAttribute("messaggioSuccesso") != null) { 
        %>
            <div style="text-align: center; padding: 20px 0;">
                <h1 style="color: yellow !important; font-size: 1.8rem; line-height: 1.4;">
                    <%= request.getAttribute("messaggioSuccesso") %>
                </h1>
                <p style="color: white; margin-top: 15px;">L'ordine è stato registrato nel database e il carrello è stato svuotato.</p>
                <div style="margin-top: 30px;">
                    <a href="${pageContext.request.contextPath}/Menu" class="btn-add-cart" style="display: inline-block; text-decoration: none; padding: 12px 30px; font-size: 1.1rem; border-radius: 30px;">
                        Torna al Menu 🍣
                    </a>
                </div>
            </div>
        <% 
            } else { 
            // Se non c'è il messaggio di successo, mostra normalmente il form originale
        %>

            <h1 class="menu-title" style="color: yellow !important;">Spedizione e Pagamento 💳</h1>
            
            <%-- FORM DI CHECKOUT: Invia i dati alla Servlet "ConfermaOrdine" tramite metodo POST per salvare l'acquisto nel database --%>
            <form id="checkoutForm" action="${pageContext.request.contextPath}/ConfermaOrdine" method="POST" style="color: white;">
                
                <h3>📍 Dati di Spedizione</h3>
                
                <div style="margin-bottom: 15px;">
                    <label>Indirizzo:</label><br>
                    <input type="text" id="indirizzo" name="indirizzo" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                    <%-- Questo span vuoto verrà usato da JavaScript per stampare il messaggio d'errore se il campo non è valido --%>
                    <span id="error-indirizzo" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                </div>

                <div style="margin-bottom: 15px;">
                    <label>Città:</label><br>
                    <input type="text" id="citta" name="citta" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                    <span id="error-citta" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                </div>

                <div style="margin-bottom: 25px;">
                    <label>CAP (5 cifre):</label><br>
                    <%-- maxlength="5" impedisce fisicamente all'utente di scrivere più di 5 caratteri --%>
                    <input type="text" id="cap" name="cap" maxlength="5" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                    <span id="error-cap" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                </div>

                <hr style="border: 0; border-top: 1px solid #333; margin: 20px 0;">

                <h3>💳 Dati della Carta</h3>
                
                <div style="margin-bottom: 15px;">
                    <label>Intestatario Carta:</label><br>
                    <input type="text" id="intestatario" name="intestatario" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                    <span id="error-intestatario" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                </div>

                <div style="margin-bottom: 15px;">
                    <label>Numero Carta (16 cifre):</label><br>
                    <input type="text" id="numeroCarta" name="numeroCarta" maxlength="16" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                    <span id="error-numeroCarta" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                </div>

                <div style="display: flex; gap: 20px; margin-bottom: 30px;">
                    <div style="flex: 1;">
                        <label>Scadenza (MM/AAAA):</label><br>
                        <input type="text" id="scadenza" name="scadenza" placeholder="MM/AAAA" maxlength="7" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                        <span id="error-scadenza" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                    </div>
                    <div style="flex: 1;">
                        <label>CVV (3 cifre):</label><br>
                        <input type="text" id="cvv" name="cvv" maxlength="3" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                        <span id="error-cvv" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
                    </div>
                </div>

                <%-- BOTTONE DI INVIO: Quando viene cliccato, si attiva l'evento "onsubmit" del form catturato dallo script validation.js --%>
                <button type="submit" class="btn-add-cart" style="width: 100%; padding: 14px; font-size: 1.1rem; border-radius: 30px;">
                    Paga e Conferma Ordine 
                </button>
                
                <% 
                    // Controllo di sicurezza lato server: se la Servlet riscontra problemi (es. carrello vuoto o errore nel DB) 
                    // rimanda indietro l'utente stampando questo messaggio rosso di errore
                    if (request.getAttribute("errorePagamento") != null) { 
                %>
                        <p style="color: red; font-weight: bold; text-align: center; margin-top: 15px;">
                            <%= request.getAttribute("errorePagamento") %>
                        </p>
                <% } %>
            </form>

        <% } // Fine blocco else %>

    </div>

    <%-- Richiamo dello script di validazione --%>
    <script src="${pageContext.request.contextPath}/scripts/validation.js"></script>
</body>
</html>
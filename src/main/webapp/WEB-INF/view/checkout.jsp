<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Checkout</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
    <script src="${pageContext.request.contextPath}/scripts/validation.js" defer></script>
</head>
<body class="menu-page">

    <div class="menu-container" style="max-width: 600px; margin-top: 50px;">
        <h1 class="menu-title" style="color: yellow !important;">Spedizione e Pagamento 💳</h1>
        
        <form id="checkoutForm" action="${pageContext.request.contextPath}/ConfermaOrdine" method="POST" style="color: white;">
            
            ### 📍 Dati di Spedizione
            <div style="margin-bottom: 15px;">
                <label>Indirizzo:</label><br>
                <input type="text" id="indirizzo" name="indirizzo" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                <span id="error-indirizzo" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
            </div>

            <div style="margin-bottom: 15px;">
                <label>Città:</label><br>
                <input type="text" id="citta" name="citta" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                <span id="error-citta" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
            </div>

            <div style="margin-bottom: 25px;">
                <label>CAP (5 cifre):</label><br>
                <input type="text" id="cap" name="cap" maxlength="5" style="width: 100%; padding: 10px; margin-top: 5px; border-radius: 5px; border: 1px solid #333; background: #222; color: white;">
                <span id="error-cap" style="color: #ff3838; font-size: 0.85rem; display: block; margin-top: 5px;"></span>
            </div>

            ---

            ### 💳 Dati della Carta
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

            <button type="submit" class="btn-add-cart" style="width: 100%; padding: 14px; font-size: 1.1rem; border-radius: 30px;">
                Paga e Conferma Ordine 
            </button>
        </form>
    </div>

</body>
</html>
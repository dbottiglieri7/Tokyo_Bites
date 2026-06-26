<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Carrello" %>
<%@ page import="model.Piatto" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Il Tuo Carrello</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page"> 
<%
    Carrello carrelloNav = (Carrello) session.getAttribute("carrello");
    int totaleElementiCarrello = (carrelloNav != null) ? carrelloNav.getElementi().size() : 0;
%>
    <nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px;">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; font-weight: bold; font-size: 1.5em;">Tokyo Bites 🍣</a>
        </div>
        
        <ul class="nav-links" style="list-style: none; display: flex; align-items: center; margin: 0; padding: 0;">
            <li><a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; margin-left: 20px;">Home 🏠</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu" style="color: white; text-decoration: none; margin-left: 20px;">Menu 🍣</a></li>
            <li>
    <a href="${pageContext.request.contextPath}/Carrello" style="color: white; text-decoration: none; margin-left: 20px;">
        Carrello 🛒 <span style="background: #ff3838; color: white; padding: 2px 7px; border-radius: 50%; font-size: 0.85rem; font-weight: bold; margin-left: 3px;"><%= totaleElementiCarrello %></span>
    </a>
</li>
            
            <%
                String utente = null;
                if (session != null) {
                    utente = (String) session.getAttribute("utenteLoggato");
                }
                if (utente != null) {
            %>
                <li><a href="${pageContext.request.contextPath}/StoricoOrdini" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">Miei Ordini 📜</a></li>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Logout (<%= utente %>) 👤</a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/Login" style="color: white; text-decoration: none; margin-left: 20px;">Login 👤</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="menu-container" id="carrello-wrapper" style="max-width: 800px;"> 
        <h1 class="menu-title" style="color: yellow !important;">Il Tuo Carrello 🛒</h1>
        
        <%
            Carrello carrello = (Carrello) session.getAttribute("carrello");
            if (carrello == null || carrello.getElementi().isEmpty()) {
        %>
            <div id="carrello-vuoto-msg" style="text-align: center; padding: 40px; color: #aaa;">
                <p style="font-size: 1.3rem; margin-bottom: 20px;">Il tuo carrello è attualmente vuoto.</p>
                <a href="${pageContext.request.contextPath}/Menu" class="tab active" style="text-decoration: none;">Torna al Menu</a>
            </div>
        <% 
            } else { 
        %>
            <div id="contenuto-carrello-attivo">
                <table style="width: 100%; border-collapse: collapse; margin-top: 20px; color: white;">
                    <thead>
                        <tr style="border-bottom: 2px solid #ff3838; text-align: left;">
                            <th style="padding: 12px;">Piatto</th>
                            <th style="padding: 12px; text-align: center;">Quantità</th>
                            <th style="padding: 12px; text-align: right;">Prezzo Totale</th>
                            <th style="padding: 12px; text-align: center;">Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            List<Integer> idVisitati = new ArrayList<>();
                            for (Piatto p : carrello.getElementi()) { 
                                if (idVisitati.contains(p.getId())) {
                                    continue;
                                }
                                
                                int quantita = 0;
                                for (Piatto rip : carrello.getElementi()) {
                                    if (rip.getId() == p.getId()) {
                                        quantita++;
                                    }
                                }
                                
                                double prezzoParziale = p.getPrezzo() * quantita;
                                idVisitati.add(p.getId());
                        %>
                            <tr id="riga-piatto-<%= p.getId() %>" style="border-bottom: 1px solid #333;">
                                <td style="padding: 15px;">
                                    <strong style="font-size: 1.1rem;"><%= p.getNome() %></strong><br>
                                    <span style="font-size: 0.85rem; color: #aaa;"><%= p.getDescrizione() %></span>
                                </td>
                                <td id="qty-<%= p.getId() %>" style="padding: 15px; text-align: center; font-size: 1.1rem; font-weight: bold; color: yellow;">
                                    x<%= quantita %>
                                </td>
                                <td id="prezzo-<%= p.getId() %>" style="padding: 15px; text-align: right; color: #ff3838; font-weight: bold; font-size: 1.1rem;">
                                    € <%= String.format("%.2f", prezzoParziale) %>
                                </td>
                                <td style="padding: 15px; text-align: center;">
                                    <button type="button" class="btn-add-cart" style="background: #333; border: 1px solid #ff3838; padding: 5px 12px; font-size: 0.85rem; border-radius: 4px; cursor: pointer;" onclick="gestisciCarrelloAjax(<%= p.getId() %>, 'rimuovi')">
                                        Rimuovi 1 ❌
                                    </button>
                                </td>
                            </tr>
                        <% 
                            } 
                        %>
                    </tbody>
                </table>

                <div style="margin-top: 30px; border-top: 2px solid #ff3838; padding-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <span style="font-size: 1.4rem; font-weight: bold;">Totale dell'Ordine:</span>
                    </div>
                    <div>
                        <span style="font-size: 1.8rem; font-weight: bold; color: yellow;">€ <span id="totale-carrello"><%= String.format("%.2f", carrello.getPrezzoTotale()) %></span></span>
                    </div>
                </div>

                <div style="margin-top: 30px; display: flex; justify-content: space-between; align-items: center;">
                    <button type="button" class="btn-add-cart" style="background: #333; border: 1px solid #aaa; padding: 12px 24px; font-size: 1rem; border-radius: 30px; cursor: pointer;" onclick="gestisciCarrelloAjax(0, 'svuota')">
                        Svuota Carrello 🗑️
                    </button>

                   <form action="${pageContext.request.contextPath}/ConfermaOrdine" method="GET" style="margin: 0;">
                        <button type="submit" class="btn-add-cart" style="padding: 12px 30px; font-size: 1.1rem; border-radius: 30px;">
                            Conferma Ordine 
                        </button>
                    </form>
                </div>
            </div>
        <% 
            } 
        %>
    </div>

    <script>
    function gestisciCarrelloAjax(idPiatto, azioneUtente) {
        const urlParams = new URLSearchParams();
        urlParams.append('idPiatto', idPiatto);
        urlParams.append('azione', azioneUtente);

        // Chiamata asincrona al server via Fetch API (AJAX)
        fetch('${pageContext.request.contextPath}/Carrello', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest' // Header fondamentale intercettato dalla servlet
            },
            body: urlParams.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Caso 1: Il carrello è diventato completamente vuoto (o l'utente ha cliccato svuota)
                if (data.carrelloVuoto) {
                    document.getElementById('carrello-wrapper').innerHTML = `
                        <h1 class="menu-title" style="color: yellow !important;">Il Tuo Carrello 🛒</h1>
                        <div style="text-align: center; padding: 40px; color: #aaa;">
                            <p style="font-size: 1.3rem; margin-bottom: 20px;">Il tuo carrello è attualmente vuoto.</p>
                            <a href="\${window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1))}/Menu" class="tab active" style="text-decoration: none;">Torna al Menu</a>
                        </div>
                    `;
                } else {
                    // Caso 2: Rimozione parziale di una quantità
                    if (data.nuovaQuantita > 0) {
                        // Aggiorna la quantità e il prezzo parziale modificando il DOM direttamente
                        document.getElementById('qty-' + data.idPiatto).innerText = 'x' + data.nuovaQuantita;
                        document.getElementById('prezzo-' + data.idPiatto).innerText = '€ ' + data.prezzoParziale;
                    } else {
                        // Se la quantità arriva a zero, elimina l'intera riga della tabella associata
                        const riga = document.getElementById('riga-piatto-' + data.idPiatto);
                        if (riga) riga.remove();
                    }
                    // In ogni caso, aggiorna il prezzo totale complessivo in fondo
                    document.getElementById('totale-carrello').innerText = data.prezzoTotale;
                }
            }
        })
        .catch(error => console.error('Errore nell\'elaborazione AJAX del carrello:', error));
    }
    </script>
</body>
</html>
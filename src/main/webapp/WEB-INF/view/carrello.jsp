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
    <%-- Collegamento al foglio di stile CSS globale --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page"> 
<%
    // Recupera l'oggetto Carrello dalla sessione per calcolare quanti elementi ci sono 
    // e aggiornare il contatore dinamico nella Navbar (es. Carrello 🛒 [3])
    Carrello carrelloNav = (Carrello) session.getAttribute("carrello");
    int totaleElementiCarrello = (carrelloNav != null) ? carrelloNav.getElementi().size() : 0;
%>
    <%-- BARRA DI NAVIGAZIONE (NAVBAR) --%>
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
                // Controllo della sessione per verificare se l'utente è loggato.
                // Se è loggato, mostra lo Storico Ordini e il tasto Logout, altrimenti mostra il tasto Login.
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

    <%-- CONTENITORE PRINCIPALE DEL CARRELLO --%>
    <div class="menu-container" id="carrello-wrapper" style="max-width: 800px;"> 
        <h1 class="menu-title" style="color: yellow !important;">Il Tuo Carrello 🛒</h1>
        
        <%
            // Recupera il carrello effettivo per ciclarne gli elementi nella tabella
            Carrello carrello = (Carrello) session.getAttribute("carrello");
            
            // CASO 1: Il carrello non esiste in sessione o non ha prodotti all'interno
            if (carrello == null || carrello.getElementi().isEmpty()) {
        %>
            <div id="carrello-vuoto-msg" style="text-align: center; padding: 40px; color: #aaa;">
                <p style="font-size: 1.3rem; margin-bottom: 20px;">Il tuo carrello è attualmente vuoto.</p>
                <a href="${pageContext.request.contextPath}/Menu" class="tab active" style="text-decoration: none;">Torna al Menu</a>
            </div>
        <% 
            } else { 
            // CASO 2: Il carrello contiene prodotti -> Mostra la tabella di riepilogo
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
                            // Lista di appoggio per evitare di duplicare le righe della tabella 
                            // se lo stesso prodotto è stato aggiunto più volte nel carrello
                            List<Integer> idVisitati = new ArrayList<>();
                            
                            for (Piatto p : carrello.getElementi()) { 
                                // Se l'ID del piatto è già stato stampato in tabella, salta questo giro del ciclo
                                if (idVisitati.contains(p.getId())) {
                                    continue;
                                }
                                
                                // Algoritmo di conteggio: calcola quante volte questo specifico piatto compare nel carrello
                                int quantita = 0;
                                for (Piatto rip : carrello.getElementi()) {
                                    if (rip.getId() == p.getId()) {
                                        quantita++;
                                    }
                                }
                                
                                // Calcola il prezzo parziale relativo alla quantità (Prezzo Unitario * Quantità)
                                double prezzoParziale = p.getPrezzo() * quantita;
                                idVisitati.add(p.getId()); // Segna il prodotto come "già stampato"
                        %>
                            <%-- ID dinamico assegnato alla riga per permettere a JavaScript/AJAX di eliminarla al volo dal DOM se la quantità scende a 0 --%>
                            <tr id="riga-piatto-<%= p.getId() %>" style="border-bottom: 1px solid #333;">
                                <td style="padding: 15px;">
                                    <strong style="font-size: 1.1rem;"><%= p.getNome() %></strong><br>
                                    <span style="font-size: 0.85rem; color: #aaa;"><%= p.getDescrizione() %></span>
                                </td>
                                <%-- ID dinamico per aggiornare via AJAX il testo della quantità (es. da x2 a x1) --%>
                                <td id="qty-<%= p.getId() %>" style="padding: 15px; text-align: center; font-size: 1.1rem; font-weight: bold; color: yellow;">
                                    x<%= quantita %>
                                </td>
                                <%-- ID dinamico per aggiornare via AJAX il prezzo totale di questa riga --%>
                                <td id="prezzo-<%= p.getId() %>" style="padding: 15px; text-align: right; color: #ff3838; font-weight: bold; font-size: 1.1rem;">
                                    € <%= String.format("%.2f", prezzoParziale) %>
                                </td>
                                <td style="padding: 15px; text-align: center;">
                                    <%-- 
                                      BOTTONE RIMUOVI: Invoca la funzione JavaScript nel file esterno.
                                      Parametri passati: (ID del piatto, Azione da compiere, Context Path per la servlet).
                                    --%>
                                    <button type="button" class="btn-add-cart" style="background: #333; border: 1px solid #ff3838; padding: 5px 12px; font-size: 0.85rem; border-radius: 4px; cursor: pointer;" 
                                            onclick="gestisciCarrelloAjax(<%= p.getId() %>, 'rimuovi', '${pageContext.request.contextPath}')">
                                        Rimuovi 1 ❌
                                    </button>
                                </td>
                            </tr>
                        <% 
                            } 
                        %>
                    </tbody>
                </table>

                <%-- SEZIONE PREZZO TOTALE COMPLESSIVO --%>
                <div style="margin-top: 30px; border-top: 2px solid #ff3838; padding-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <span style="font-size: 1.4rem; font-weight: bold;">Totale dell'Ordine:</span>
                    </div>
                    <div>
                        <%-- Lo span 'totale-carrello' viene intercettato da JavaScript per aggiornare l'importo totale in tempo reale --%>
                        <span style="font-size: 1.8rem; font-weight: bold; color: yellow;">€ <span id="totale-carrello"><%= String.format("%.2f", carrello.getPrezzoTotale()) %></span></span>
                    </div>
                </div>

                <%-- SEZIONE BOTTONI DI AZIONE FINALI --%>
                <div style="margin-top: 30px; display: flex; justify-content: space-between; align-items: center;">
                    <%-- 
                      BOTTONE SVUOTA: Invoca la funzione JavaScript passando ID 0 e l'azione 'svuota' 
                      per ripulire l'intera sessione tramite chiamata AJAX.
                    --%>
                    <button type="button" class="btn-add-cart" style="background: #333; border: 1px solid #aaa; padding: 12px 24px; font-size: 1rem; border-radius: 30px; cursor: pointer;" 
                            onclick="gestisciCarrelloAjax(0, 'svuota', '${pageContext.request.contextPath}')">
                        Svuota Carrello 🗑️
                    </button>

                    <%-- Form standard che reindirizza l'utente alla Servlet di checkout/conferma ordine --%>
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
    
    <%-- INCLUSIONE DEL FILE JAVASCRIPT ESTERNO --%>
    <script src="${pageContext.request.contextPath}/scripts/carrello.js"></script>
</body>
</html>
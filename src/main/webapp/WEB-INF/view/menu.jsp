<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Piatto" %>
<%@ page import="model.Carrello" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tokyo Bites - Menu</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page"> 
    <%
        // Recupera il carrello dalla sessione per calcolare quanti elementi ci sono dentro
        // e aggiornare il numerino rosso (badge) nel carrello in alto
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
            <li><a href="${pageContext.request.contextPath}/Menu" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Menu 🍣</a></li>
            
            <li>
                <%-- Il link del carrello ha degli ID speciali ('badge-carrello') che JavaScript userà per cambiare il numerino al volo senza ricaricare la pagina --%>
                <a href="${pageContext.request.contextPath}/Carrello" id="nav-carrello-link" style="color: white; text-decoration: none; margin-left: 20px;">
                    Carrello 🛒 <span id="badge-carrello" style="background: #ff3838; color: white; padding: 2px 7px; border-radius: 50%; font-size: 0.85rem; font-weight: bold; margin-left: 3px;"><%= totaleElementiCarrello %></span>
                </a>
            </li>
            
            <%
                // Controllo se l'utente ha fatto il login. Se sì, mostro "Miei Ordini" e "Logout", altrimenti solo "Login"
                String utente = null;
                if (session != null) {
                    utente = (String) session.getAttribute("utenteLoggato");
                }
                
                if (utente != null) {
            %>
                <li><a href="${pageContext.request.contextPath}/StoricoOrdini" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">Miei Ordini 📜</a></li>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Logout (<%= utente %>) 👤</a></li>
            <% 
                } else { 
            %>
                <li><a href="${pageContext.request.contextPath}/Login" style="color: white; text-decoration: none; margin-left: 20px;">Login 👤</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="menu-container">
        <%-- Stampa il titolo della categoria recuperata dalla Servlet (es: "Sushi e Sashimi") --%>
        <h1 class="menu-title">Il Nostro Menu: <%= request.getAttribute("categoriaAttuale") %></h1>
        
        <%
            String catAttuale = (String) request.getAttribute("categoriaAttuale");
            if (catAttuale == null) catAttuale = "Sushi e Sashimi";
        %>
        
        <%-- BOTTONI DELLE CATEGORIE: Se la categoria del bottone corrisponde a quella attuale, aggiunge la classe CSS "active" per colorarlo --%>
        <div class="categories-tabs">
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Sushi%20e%20Sashimi" class="tab <%= catAttuale.equals("Sushi e Sashimi") ? "active" : "" %>">Sushi e Sashimi</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=SushiRoll" class="tab <%= catAttuale.equals("SushiRoll") ? "active" : "" %>">SushiRoll</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Ravioli%20e%20Sfizi" class="tab <%= catAttuale.equals("Ravioli e Sfizi") ? "active" : "" %>">Ravioli e Sfizi</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Primi%20Piatti" class="tab <%= catAttuale.equals("Primi Piatti") ? "active" : "" %>">Primi Piatti</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Noodles" class="tab <%= catAttuale.equals("Noodles") ? "active" : "" %>">Noodles</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Antipasti" class="tab <%= catAttuale.equals("Antipasti") ? "active" : "" %>">Antipasti</a>
		    <a href="${pageContext.request.contextPath}/Menu?categoria=Bevande" class="tab <%= catAttuale.equals("Bevande") ? "active" : "" %>">Bevande</a>
		</div>

        <%-- BARRA DI RICERCA: Ogni volta che l'utente scrive una lettera (oninput), parte la funzione JS per nascondere i piatti che non c'entrano --%>
        <div class="search-container">
            <input type="text" 
                   id="menu-search" 
                   class="search-input" 
                   placeholder="Cerca un piatto in questa categoria... 🔍" 
                   oninput="filtraPiattiInTempoReale()"
                   onkeydown="if(event.key === 'Enter') event.preventDefault();">
        </div>

        <%-- GRIGLIA DEI PRODOTTI --%>
        <div class="products-grid" id="products-grid">
            <%
                // Recupera la lista dei piatti mandata dalla Servlet
                List<Piatto> prodotti = (List<Piatto>) request.getAttribute("prodottiMenu");
                if (prodotti != null && !prodotti.isEmpty()) {
                    // Ciclo per stampare la scheda (card) di ogni singolo piatto
                    for (Piatto p : prodotti) {
                        
                        // Algoritmo di controllo: conta quante unità di QUESTO piatto ci sono già nel carrello
                        int quantitaGiaPresente = 0;
                        if (carrelloNav != null) {
                            for (Piatto item : carrelloNav.getElementi()) {
                                if (item.getId() == p.getId()) {
                                    quantitaGiaPresente++;
                                }
                            }
                        }
            %>
                <%-- Classe speciale 'product-card-item' usata da JS per fare la ricerca di testo in tempo reale --%>
                <div class="product-card product-card-item">
                    <div class="product-image-box">
                        <%-- Cliccando sull'immagine si attiva il pop-up per vederla ingrandita --%>
                        <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>" 
                             alt="<%= p.getNome() %>" 
                             class="product-img" 
                             onclick="apriIngrandimento(this.src, '<%= p.getNome().replace("'", "\\'") %>')">
                    </div>
                    <div class="product-info">
                        <%-- 'target-name' serve a JS per capire se il nome del piatto corrisponde a quello cercato nella barra --%>
                        <h3 class="product-name target-name">
                            <%= p.getNome() %>
                            <%-- Questa scritta (es. x2) compare in giallo affianco al nome solo se il piatto è già nel carrello --%>
                            <span id="moltiplicatore-<%= p.getId() %>" style="color: yellow; margin-left: 5px; font-size: 1rem; <%= (quantitaGiaPresente > 0) ? "" : "display:none;" %>">
                                (x<%= quantitaGiaPresente %>)
                            </span>
                        </h3>
                        <p class="product-desc"><%= p.getDescrizione() %></p>
                        
                        <div class="product-footer">
                            <span class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></span>
                            
                            <%-- FORM DI AGGIUNTA AL CARRELLO: Gestito via AJAX (onsubmit) per evitare il caricamento totale della pagina --%>
                            <form action="${pageContext.request.contextPath}/Carrello" method="POST" style="margin: 0;" onsubmit="aggiungiAlCarrelloAjax(event, this, <%= p.getId() %>)">
                                <input type="hidden" name="idPiatto" value="<%= p.getId() %>">
                                <input type="hidden" name="categoriaProvenienza" value="<%= catAttuale %>">
                                <button type="submit" class="btn-add-cart">Aggiungi</button>
                            </form>
                        </div>
                    </div>
                </div>
            <% 
                    }
                } else {
            %>
                <div style="color: white; text-align: center; grid-column: 1 / -1; padding: 40px;">
                    <p>Nessun piatto trovato in questa categoria.</p>
                </div>
            <% } %>
        </div>
    </div>

    <%-- Sfondo nero oscurato (Finestra Modale) che si attiva quando si clicca sulla foto di un piatto per ingrandirla --%>
    <div id="image-zoom-modal" class="image-modal" onclick="chiudiIngrandimento()">
        <img class="modal-content" id="img-target-zoom">
        <div id="modal-caption" class="modal-caption"></div>
    </div>
    
    <%-- Inclusione del file JavaScript esterno dedicato alla logica della pagina del Menu --%>
	<script src="${pageContext.request.contextPath}/scripts/menu.js"></script>
    
</body>
</html>
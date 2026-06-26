<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Piatto" %>
<%@ page import="model.Carrello" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Menu</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page"> 
    <%
        // Recuperiamo il carrello per calcolare dinamicamente contatori e moltiplicatori all'avvio della pagina
        Carrello carrelloNav = (Carrello) session.getAttribute("carrello");
        int totaleElementiCarrello = (carrelloNav != null) ? carrelloNav.getElementi().size() : 0;
    %>

    <nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px;">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; font-weight: bold; font-size: 1.5em;">Tokyo Bites 🍣</a>
        </div>
        
        <ul class="nav-links" style="list-style: none; display: flex; align-items: center; margin: 0; padding: 0;">
            <li><a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; margin-left: 20px;">Home 🏠</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Menu 🍣</a></li>
            
            <li>
                <a href="${pageContext.request.contextPath}/Carrello" id="nav-carrello-link" style="color: white; text-decoration: none; margin-left: 20px;">
                    Carrello 🛒 <span id="badge-carrello" style="background: #ff3838; color: white; padding: 2px 7px; border-radius: 50%; font-size: 0.85rem; font-weight: bold; margin-left: 3px;"><%= totaleElementiCarrello %></span>
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
            <% 
                } else { 
            %>
                <li><a href="${pageContext.request.contextPath}/Login" style="color: white; text-decoration: none; margin-left: 20px;">Login 👤</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="menu-container">
        <h1 class="menu-title">Il Nostro Menu: <%= request.getAttribute("categoriaAttuale") %></h1>
        
        <%
            String catAttuale = (String) request.getAttribute("categoriaAttuale");
            if (catAttuale == null) catAttuale = "Sushi e Sashimi";
        %>
        
        <div class="categories-tabs">
            <a href="${pageContext.request.contextPath}/Menu?categoria=Sushi e Sashimi" 
               class="tab <%= catAttuale.equalsIgnoreCase("Sushi e Sashimi") ? "active" : "" %>">Sushi & Sashimi</a>
               
            <a href="${pageContext.request.contextPath}/Menu?categoria=SushiRoll" 
               class="tab <%= catAttuale.equalsIgnoreCase("SushiRoll") ? "active" : "" %>">Sushi Roll</a>
            
            <a href="${pageContext.request.contextPath}/Menu?categoria=Ravioli e Sfizi" 
               class="tab <%= catAttuale.equalsIgnoreCase("Ravioli e Sfizi") ? "active" : "" %>">Ravioli & Sfizi</a>
            
            <a href="${pageContext.request.contextPath}/Menu?categoria=Primi Piatti" 
               class="tab <%= catAttuale.equalsIgnoreCase("Primi Piatti") ? "active" : "" %>">Primi Piatti</a>
               
            <a href="${pageContext.request.contextPath}/Menu?categoria=Noodles" 
               class="tab <%= catAttuale.equalsIgnoreCase("Noodles") ? "active" : "" %>">Noodles</a>
               
            <a href="${pageContext.request.contextPath}/Menu?categoria=Antipasti" 
               class="tab <%= catAttuale.equalsIgnoreCase("Antipasti") ? "active" : "" %>">Antipasti</a>
               
            <a href="${pageContext.request.contextPath}/Menu?categoria=Bevande" 
               class="tab <%= catAttuale.equalsIgnoreCase("Bevande") ? "active" : "" %>">Bevande</a>
        </div>

        <div class="products-grid">
            <%
                List<Piatto> prodotti = (List<Piatto>) request.getAttribute("prodottiMenu");
                if (prodotti != null && !prodotti.isEmpty()) {
                    for (Piatto p : prodotti) {
                        
                        // PUNTO 2: Calcoliamo quanti elementi di QUESTO specifico piatto ci sono già nel carrello
                        int quantitaGiaPresente = 0;
                        if (carrelloNav != null) {
                            for (Piatto item : carrelloNav.getElementi()) {
                                if (item.getId() == p.getId()) {
                                    quantitaGiaPresente++;
                                }
                            }
                        }
            %>
                <div class="product-card">
                    <div class="product-image-box">
                        <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>" alt="<%= p.getNome() %>" class="product-img">
                    </div>
                    <div class="product-info">
                        <h3 class="product-name">
                            <%= p.getNome() %>
                            <span id="moltiplicatore-<%= p.getId() %>" style="color: yellow; margin-left: 5px; font-size: 1rem; <%= (quantitaGiaPresente > 0) ? "" : "display:none;" %>">
                                (x<%= quantitaGiaPresente %>)
                            </span>
                        </h3>
                        <p class="product-desc"><%= p.getDescrizione() %></p>
                        
                        <div class="product-footer">
                            <span class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></span>
                            
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

    <script>
        function aggiungiAlCarrelloAjax(event, formElement, idPiatto) {
            // Blocca il caricamento e redirect nativo della pagina
            event.preventDefault();

            const formData = new URLSearchParams(new FormData(formElement));

            // Esegue l'invio asincrono strutturato
            fetch(formElement.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest' // Dice alla Servlet di rispondere in formato JSON
                },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 1. Incrementa il badge numerico totale della navbar (Punto 3)
                    const badge = document.getElementById('badge-carrello');
                    let conteggioAttuale = parseInt(badge.innerText, 10);
                    badge.innerText = conteggioAttuale + 1;

                    // 2. Aggiorna o mostra il moltiplicatore (x1, x2) vicino al nome del prodotto (Punto 2)
                    const moltiplicatoreSpan = document.getElementById('moltiplicatore-' + idPiatto);
                    if (moltiplicatoreSpan) {
                        moltiplicatoreSpan.innerText = '(x' + data.nuovaQuantita + ')';
                        moltiplicatoreSpan.style.display = 'inline-block';
                    }
                }
            })
            .catch(error => console.error('Errore nell\'aggiunta AJAX dal menu:', error));
        }

        // Script per preservare la posizione dello scroll nativo
        window.addEventListener('beforeunload', function() {
            localStorage.setItem('scrollPositionMenu', window.scrollY);
        });

        window.addEventListener('DOMContentLoaded', function() {
            const savedPosition = localStorage.getItem('scrollPositionMenu');
            if (savedPosition) {
                window.scrollTo(0, parseInt(savedPosition, 10));
                localStorage.removeItem('scrollPositionMenu');
            }
        });
    </script>
</body>
</html>
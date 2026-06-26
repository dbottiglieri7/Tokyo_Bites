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
            <a href="${pageContext.request.contextPath}/Menu?categoria=Sushi e Sashimi" class="tab <%= catAttuale.equalsIgnoreCase("Sushi e Sashimi") ? "active" : "" %>">Sushi & Sashimi</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=SushiRoll" class="tab <%= catAttuale.equalsIgnoreCase("SushiRoll") ? "active" : "" %>">Sushi Roll</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=Ravioli e Sfizi" class="tab <%= catAttuale.equalsIgnoreCase("Ravioli e Sfizi") ? "active" : "" %>">Ravioli & Sfizi</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=Primi Piatti" class="tab <%= catAttuale.equalsIgnoreCase("Primi Piatti") ? "active" : "" %>">Primi Piatti</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=Noodles" class="tab <%= catAttuale.equalsIgnoreCase("Noodles") ? "active" : "" %>">Noodles</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=Antipasti" class="tab <%= catAttuale.equalsIgnoreCase("Antipasti") ? "active" : "" %>">Antipasti</a>
            <a href="${pageContext.request.contextPath}/Menu?categoria=Bevande" class="tab <%= catAttuale.equalsIgnoreCase("Bevande") ? "active" : "" %>">Bevande</a>
        </div>

        <div class="search-container">
            <input type="text" 
                   id="menu-search" 
                   class="search-input" 
                   placeholder="Cerca un piatto in questa categoria... 🔍" 
                   oninput="filtraPiattiInTempoReale()"
                   onkeydown="if(event.key === 'Enter') event.preventDefault();">
        </div>

        <div class="products-grid" id="products-grid">
            <%
                List<Piatto> prodotti = (List<Piatto>) request.getAttribute("prodottiMenu");
                if (prodotti != null && !prodotti.isEmpty()) {
                    for (Piatto p : prodotti) {
                        int quantitaGiaPresente = 0;
                        if (carrelloNav != null) {
                            for (Piatto item : carrelloNav.getElementi()) {
                                if (item.getId() == p.getId()) {
                                    quantitaGiaPresente++;
                                }
                            }
                        }
            %>
                <div class="product-card product-card-item">
                    <div class="product-image-box">
                        <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>" 
                             alt="<%= p.getNome() %>" 
                             class="product-img" 
                             onclick="apriIngrandimento(this.src, '<%= p.getNome().replace("'", "\\'") %>')">
                    </div>
                    <div class="product-info">
                        <h3 class="product-name target-name">
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

    <div id="image-zoom-modal" class="image-modal" onclick="chiudiIngrandimento()">
        <img class="modal-content" id="img-target-zoom">
        <div id="modal-caption" class="modal-caption"></div>
    </div>

    <script>
        // Filtro di ricerca Istantaneo e reattivo a ogni singola lettera
        function filtraPiattiInTempoReale() {
            const barraRicerca = document.getElementById('menu-search');
            const filtro = barraRicerca.value.toLowerCase().trim();
            const schedePiatti = document.getElementsByClassName('product-card-item');

            for (let i = 0; i < schedePiatti.length; i++) {
                const tagNome = schedePiatti[i].querySelector('.target-name');
                if (tagNome) {
                    // Cloniamo il nodo per poter manipolare il testo in sicurezza senza rompere la pagina
                    const copiaNodo = tagNome.cloneNode(true);
                    const spanInterno = copiaNodo.querySelector('span');
                    if (spanInterno) {
                        copiaNodo.removeChild(spanInterno); // Rimuoviamo il tag (x1, x2) dal calcolo del filtro
                    }
                    
                    const testoNomePulito = copiaNodo.textContent.toLowerCase().trim();
                    
                    if (testoNomePulito.includes(filtro)) {
                        schedePiatti[i].style.display = "";
                    } else {
                        schedePiatti[i].style.display = "none";
                    }
                }
            }
        }

        // Modal Ingrandimento Immagine
        function apriIngrandimento(srcImmagine, nomePiatto) {
            const modal = document.getElementById('image-zoom-modal');
            const modalImg = document.getElementById('img-target-zoom');
            const caption = document.getElementById('modal-caption');
            
            modal.style.display = "block";
            modalImg.src = srcImmagine;
            caption.innerHTML = nomePiatto;
        }

        function chiudiIngrandimento() {
            document.getElementById('image-zoom-modal').style.display = "none";
        }

        // Script AJAX per l'aggiunta dei piatti
        function aggiungiAlCarrelloAjax(event, formElement, idPiatto) {
            event.preventDefault();
            const formData = new URLSearchParams(new FormData(formElement));

            fetch(formElement.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const badge = document.getElementById('badge-carrello');
                    let conteggioAttuale = parseInt(badge.innerText, 10);
                    badge.innerText = conteggioAttuale + 1;

                    const moltiplicatoreSpan = document.getElementById('moltiplicatore-' + idPiatto);
                    if (moltiplicatoreSpan) {
                        moltiplicatoreSpan.innerText = '(x' + data.nuovaQuantita + ')';
                        moltiplicatoreSpan.style.display = 'inline-block';
                    }
                }
            })
            .catch(error => console.error('Errore nell\'aggiunta AJAX dal menu:', error));
        }

        // Script posizione scroll nativo
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
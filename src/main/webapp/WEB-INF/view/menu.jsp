<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Piatto" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Menu</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body class="menu-page"> 
    <nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px;">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; font-weight: bold; font-size: 1.5em;">Tokyo Bites 🍣</a>
        </div>
        
        <ul class="nav-links" style="list-style: none; display: flex; align-items: center; margin: 0; padding: 0;">
            <li><a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; margin-left: 20px;">Home 🏠</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Menu 🍣</a></li>
            <li><a href="${pageContext.request.contextPath}/Carrello" style="color: white; text-decoration: none; margin-left: 20px;">Carrello 🛒</a></li>
            
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
            %>
                <div class="product-card">
                    <div class="product-image-box">
                        <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>" alt="<%= p.getNome() %>" class="product-img">
                    </div>
                    <div class="product-info">
                        <h3 class="product-name"><%= p.getNome() %></h3>
                        <p class="product-desc"><%= p.getDescrizione() %></p>
                        
                        <div class="product-footer">
                            <span class="product-price">€ <%= String.format("%.2f", p.getPrezzo()) %></span>
                            
                            <form action="${pageContext.request.contextPath}/Carrello" method="POST" style="margin: 0;">
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
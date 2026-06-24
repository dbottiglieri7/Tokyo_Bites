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
    <nav class="navbar">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home">Tokyo Bites 🍣</a>
        </div>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/Home">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu" class="active">Menu</a></li>
            <li><a href="${pageContext.request.contextPath}/Carrello">Carrello</a></li>
            
            <%
                String utente = null;
                if (session != null) {
                    utente = (String) session.getAttribute("utenteLoggato");
                }
                if (utente == null) {
            %>
                <li><a href="${pageContext.request.contextPath}/Login">Login</a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; font-weight: bold;">Logout (<%= utente %>)</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="menu-container">
        <h1 class="menu-title">Il Nostro Menu: <%= request.getAttribute("categoriaAttuale") %></h1>
        
        <%
            String catAttuale = (String) request.getAttribute("categoriaAttuale");
            if (catAttuale == null) catAttuale = "Bevande";
        %>
        
        <div class="categories-tabs">
            <a href="${pageContext.request.contextPath}/Menu?categoria=Bevande" 
               class="tab <%= catAttuale.equalsIgnoreCase("Bevande") ? "active" : "" %>">Bevande</a>
            
            <a href="${pageContext.request.contextPath}/Menu?categoria=SushiRoll" 
               class="tab <%= catAttuale.equalsIgnoreCase("SushiRoll") ? "active" : "" %>">Sushi Roll</a>
            
            <a href="${pageContext.request.contextPath}/Menu?categoria=Antipasti" 
               class="tab <%= catAttuale.equalsIgnoreCase("Antipasti") ? "active" : "" %>">Antipasti</a>
               
            <a href="${pageContext.request.contextPath}/Menu?categoria=Noodles" 
               class="tab <%= catAttuale.equalsIgnoreCase("Noodles") ? "active" : "" %>">Noodles</a>
        </div>

        <div class="products-grid">
            <%
                List<Piatto> prodotti = (List<Piatto>) request.getAttribute("prodottiMenu");
                if (prodotti != null) {
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
                } 
            %>
        </div>
    </div>

</body>
</html>
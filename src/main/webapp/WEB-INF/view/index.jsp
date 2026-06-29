<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Carrello" %>
<!DOCTYPE html>
<html>
<head> 
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tokyo Bites</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>
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
            
            <%-- Controllo dinamico della sessione --%>
            <% 
                String utente = (String) session.getAttribute("utenteLoggato");
                if (utente != null) { 
            %>
                <li><a href="${pageContext.request.contextPath}/StoricoOrdini" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">Miei Ordini 📜</a></li>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Logout (<%= utente %>) 👤</a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/Login" style="color: white; text-decoration: none; margin-left: 20px;">Login 👤</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="content" style="text-align: center; margin-top: 100px; color: white; font-family: sans-serif;">
        <h1 style="font-size: 3rem; margin-bottom: 20px;">Benvenuto su Tokyo Bites! 🥢</h1>
        <p style="font-size: 1.3rem; color: white; margin-bottom: 40px;">Il miglior sushi artigianale, pronto per essere ordinato!</p>
        <a href="${pageContext.request.contextPath}/Menu" class="btn-add-cart" style="padding: 15px 40px; font-size: 1.2rem; text-decoration: none; border-radius: 30px;">
            Sfoglia il Menu 🍣
        </a>
    </div>

</body>
</html>
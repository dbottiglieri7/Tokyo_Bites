<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Ordine" %>
<%@ page import="model.Carrello" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>I Miei Ordini - Tokyo Bites</title>
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

    <div class="menu-container" style="max-width: 900px; margin-top: 40px;">
        <h1 class="menu-title" style="color: yellow !important; text-align: left; border-bottom: 2px solid #ff3838; padding-bottom: 10px;">Il Tuo Storico Ordini 📜</h1>

        <%
            // Recupero sicuro della lista ordini passata dalla Servlet
            List<Ordine> listaOrdini = (List<Ordine>) request.getAttribute("listaOrdini");
            if (listaOrdini == null || listaOrdini.isEmpty()) {
        %>
            <div style="text-align: center; padding: 40px; color: #aaa;">
                <p style="font-size: 1.3rem;">Non hai ancora effettuato nessun ordine. Corri a riempire il carrello! 🍣</p>
                <br>
                <a href="${pageContext.request.contextPath}/Menu" class="tab active" style="text-decoration: none;">Vai al Menu</a>
            </div>
        <%
            } else {
        %>
            <table style="width: 100%; border-collapse: collapse; margin-top: 20px; color: white;">
                <thead>
                    <tr style="border-bottom: 2px solid #ff3838; text-align: left; color: yellow;">
                        <th style="padding: 12px;">ID Ordine</th>
                        <th style="padding: 12px;">Data</th>
                        <th style="padding: 12px;">Spedito a</th>
                        <th style="padding: 12px; text-align: center;">Stato</th>
                        <th style="padding: 12px; text-align: right;">Totale Speso</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Ordine ordine : listaOrdini) { %>
                        <tr style="border-bottom: 1px solid #333;">
                            <td style="padding: 15px; font-weight: bold;">#<%= ordine.getId() %></td>
                            <td style="padding: 15px; color: #ccc;"><%= ordine.getDataOrdine() %></td>
                            <td style="padding: 15px; font-size: 0.95rem;">
                                <%= ordine.getIndirizzo() %>, <%= ordine.getCitta() %> (<%= ordine.getCap() %>)
                            </td>
                            <td style="padding: 15px; text-align: center;">
                                <span style="background: #e67e22; padding: 5px 10px; border-radius: 4px; font-size: 0.85rem; font-weight: bold;">
                                    <%= ordine.getStato() %>
                                </span>
                            </td>
                            <td style="padding: 15px; text-align: right; color: #2ecc71; font-weight: bold; font-size: 1.1rem;">
                                <%= String.format("%.2f", ordine.getTotale()) %> €
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <%
            }
        %>
    </div>

</body>
</html>
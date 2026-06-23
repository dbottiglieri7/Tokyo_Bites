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
    <nav class="navbar">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home">Tokyo Bites 🍣</a>
        </div>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/Home">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu">Menu</a></li>
            <li><a href="${pageContext.request.contextPath}/Carrello" class="active">Carrello</a></li>
            
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

    <div class="menu-container" style="max-width: 800px;"> 
        <h1 class="menu-title" style="color: yellow !important;">Il Tuo Carrello 🛒</h1>
        
        <%
            Carrello carrello = (Carrello) session.getAttribute("carrello");
            if (carrello == null || carrello.getElementi().isEmpty()) {
        %>
            <div style="text-align: center; padding: 40px; color: #aaa;">
                <p style="font-size: 1.3rem; margin-bottom: 20px;">Il tuo carrello è attualmente vuoto.</p>
                <a href="${pageContext.request.contextPath}/Menu" class="tab active" style="text-decoration: none;">Torna al Menu</a>
            </div>
        <% 
            } else { 
        %>
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
                        // Creiamo una lista di ID già visitati per non duplicare le righe nella tabella
                        List<Integer> idVisitati = new ArrayList<>();
                        
                        for (Piatto p : carrello.getElementi()) { 
                            // Se abbiamo già stampato questo piatto, lo saltiamo
                            if (idVisitati.contains(p.getId())) {
                                continue;
                            }
                            
                            // Contiamo quante volte questo specifico piatto compare nel carrello
                            int quantita = 0;
                            for (Piatto rip : carrello.getElementi()) {
                                if (rip.getId() == p.getId()) {
                                    quantita++;
                                }
                            }
                            
                            // Calcoliamo il prezzo parziale (prezzo * quantita)
                            double prezzoParziale = p.getPrezzo() * quantita;
                            
                            // Aggiungiamo l'ID alla lista di quelli già mostrati
                            idVisitati.add(p.getId());
                    %>
                        <tr style="border-bottom: 1px solid #333;">
                            <td style="padding: 15px;">
                                <strong style="font-size: 1.1rem;"><%= p.getNome() %></strong><br>
                                <span style="font-size: 0.85rem; color: #aaa;"><%= p.getDescrizione() %></span>
                            </td>
                            <td style="padding: 15px; text-align: center; font-size: 1.1rem; font-weight: bold; color: yellow;">
                                x<%= quantita %>
                            </td>
                            <td style="padding: 15px; text-align: right; color: #ff3838; font-weight: bold; font-size: 1.1rem;">
                                € <%= String.format("%.2f", prezzoParziale) %>
                            </td>
                            <td style="padding: 15px; text-align: center;">
                                <form action="${pageContext.request.contextPath}/Carrello" method="POST" style="margin: 0;">
                                    <input type="hidden" name="idPiatto" value="<%= p.getId() %>">
                                    <input type="hidden" name="azione" value="rimuovi">
                                    <button type="submit" class="btn-add-cart" style="background: #333; border: 1px solid #ff3838; padding: 5px 12px; font-size: 0.85rem; border-radius: 4px;">
                                        Rimuovi 1 ❌
                                    </button>
                                </form>
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
                    <span style="font-size: 1.8rem; font-weight: bold; color: yellow;">€ <%= String.format("%.2f", carrello.getPrezzoTotale()) %></span>
                </div>
            </div>

            <div style="margin-top: 30px; display: flex; justify-content: space-between; align-items: center;">
                <form action="${pageContext.request.contextPath}/Carrello" method="POST" style="margin: 0;">
                    <input type="hidden" name="azione" value="svuota">
                    <button type="submit" class="btn-add-cart" style="background: #333; border: 1px solid #aaa; padding: 12px 24px; font-size: 1rem; border-radius: 30px;">
                        Svuota Carrello 🗑️
                    </button>
                </form>

                <button class="btn-add-cart" style="padding: 12px 30px; font-size: 1.1rem; border-radius: 30px;">
                    Conferma Ordine 🚀
                </button>
            </div>
        <% 
            } 
        %>
    </div>

</body>
</html>
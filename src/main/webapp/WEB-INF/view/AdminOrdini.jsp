<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Visualizza Ordini - Tokyo Bites</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>

    <main class="admin-container">
        <h2>Console Amministratore</h2>
        
        <!-- Sottomenu di Navigazione Admin stilizzato (uguale al catalogo) -->
        <div class="admin-subnav" style="margin-bottom: 30px; padding-bottom: 15px; border-bottom: 1px solid #333;">
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaCatalogo" 
               style="color: #cccccc !important; text-decoration: none !important; font-weight: bold; font-size: 1.1rem; margin-right: 20px;"
               onmouseover="this.style.color='#ff3838'" onmouseout="this.style.color='#cccccc'">
               Gestione Catalogo
            </a> | 
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaOrdini" 
               style="color: #ffffff !important; text-decoration: none !important; font-weight: bold; font-size: 1.1rem; margin-right: 20px;">
               Visualizza Ordini
            </a> | 
            <a href="${pageContext.request.contextPath}/Logout" 
               style="color: #ff4d4d !important; text-decoration: none !important; font-weight: bold; font-size: 1.1rem;"
               onmouseover="this.style.color='#ff0000'" onmouseout="this.style.color='#ff4d4d'">
               Logout
            </a>
        </div>

        <h3>Filtra gli Ordini Ricevuti</h3>
        
        <!-- Form Filtri compatto orizzontale in linea -->
        <form action="${pageContext.request.contextPath}/AdminDashboard" method="get" class="admin-form-inline">
            <input type="hidden" name="azione" value="visualizzaOrdini">
            
            <div class="form-group">
                <label for="dataInizio">Data Inizio:</label>
                <input type="date" id="dataInizio" name="dataInizio">
            </div>
            
            <div class="form-group">
                <label for="dataFine">Data Fine:</label>
                <input type="date" id="dataFine" name="dataFine">
            </div>
            
            <div class="form-group">
                <label for="emailCliente">Email Cliente:</label>
                <input type="email" id="emailCliente" name="emailCliente" placeholder="Es. cliente@email.com">
            </div>
            
            <button type="submit" class="btn-login" style="width: auto; margin-top: 0; padding: 11px 25px;">
                Filtra Ordini
            </button>
        </form>

        <h3>Elenco Ordini Ricevuti</h3>
        
        <!-- Tabella Ordini Leggibile e coordinata -->
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th style="width: 8%;">ID</th>
                        <th>Cliente (Email)</th>
                        <th>Data Ordine</th>
                        <th>Totale Speso</th>
                        <th>Stato</th>
                        <th>Indirizzo Spedizione</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        Object ordiniObj = request.getAttribute("ordini");
                        if (ordiniObj instanceof java.util.List) {
                            java.util.List<?> ordini = (java.util.List<?>) ordiniObj;
                            if (!ordini.isEmpty()) {
                                for (Object obj : ordini) {
                                    if (obj instanceof model.Ordine) {
                                        model.Ordine o = (model.Ordine) obj;
                    %>
                                <tr>
                                    <td><strong>#<%= o.getId() %></strong></td>
                                    <td><%= o.getUtenteEmail() %></td>
                                    <td><%= o.getDataOrdine() %></td>
                                    <td style="color: #ff3838; font-weight: bold;"><%= String.format("%.2f", o.getTotale()) %> €</td>
                                    <td>
                                        <span style="background: #222; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; border: 1px solid #ff3838; color: yellow;">
                                            <%= o.getStato() %>
                                        </span>
                                    </td>
                                    <td style="font-size: 0.85rem; color: #ccc;">
                                        <%= o.getIndirizzo() %>, <%= o.getCitta() %> (<%= o.getCap() %>)
                                    </td>
                                </tr>
                    <% 
                                    }
                                }
                            } else { 
                    %>
                            <tr>
                                <td colspan="6" style="text-align: center; color: #aaa; padding: 20px;">
                                    Nessun ordine trovato con i filtri selezionati.
                                </td>
                            </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="6" style="text-align: center; color: #aaa; padding: 20px;">
                                Caricamento degli ordini fallito o non pervenuto.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

</body>
</html>
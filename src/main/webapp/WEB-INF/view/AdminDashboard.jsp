<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Tokyo Bites</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>

    <main class="admin-container">
        <h2>Console Amministratore</h2>
        
        <!-- Sottomenu di Navigazione Admin stilizzato -->
        <div class="admin-subnav">
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaCatalogo">Gestione Catalogo</a> | 
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaOrdini">Visualizza Ordini</a> | 
            <a href="${pageContext.request.contextPath}/Logout" style="color: #ff4d4d;">Logout</a>
        </div>

        <h3>Aggiungi Nuovo Prodotto nel Catalogo</h3>
        
        <!-- Form compatto con sfondo scuro integrato -->
        <form action="${pageContext.request.contextPath}/AdminDashboard" method="post" class="admin-form-inline">
            <input type="hidden" name="azione" value="inserisci">
            
            <div class="form-group">
                <label for="nome">Nome Piatto:</label>
                <input type="text" id="nome" name="nome" required placeholder="Es. Hosomaki Sake">
            </div>
            
            <div class="form-group">
                <label for="prezzo">Prezzo (€):</label>
                <input type="number" id="prezzo" name="prezzo" step="0.01" required placeholder="Es. 4.50">
            </div>
            
            <div class="form-group">
                <label for="categoria">Categoria:</label>
                <select id="categoria" name="categoria">
                    <option value="Sushi e Sashimi">Sushi e Sashimi</option>
                    <option value="SushiRoll">SushiRoll</option>
                    <option value="Bevande">Bevande</option>
                    <option value="Noodles">Noodles</option>
                    <option value="Antipasti">Antipasti</option>
                    <option value="Ravioli e Sfizi">Ravioli e Sfizi</option>
                </select>
            </div>
            
            <button type="submit" class="btn-login" style="width: auto; margin-top: 0; padding: 11px 25px;">
                Inserisci Piatto
            </button>
        </form>

        <h3>Catalogo Attuale</h3>
        
        <!-- Tabella responsive -->
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th style="width: 8%;">ID</th>
                        <th>Nome</th>
                        <th style="width: 15%;">Prezzo</th>
                        <th style="width: 20%;">Categoria</th>
                        <th style="width: 15%;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        // Utilizziamo un recupero generico per evitare crash di cast ed errori 500
                        Object catalogoObj = request.getAttribute("catalogo");
                        if (catalogoObj instanceof java.util.List) {
                            java.util.List<?> catalogo = (java.util.List<?>) catalogoObj;
                            if (!catalogo.isEmpty()) {
                                for (Object obj : catalogo) {
                                    if (obj instanceof model.Piatto) {
                                        model.Piatto p = (model.Piatto) obj;
                    %>
                                <tr>
                                    <td><strong><%= p.getId() %></strong></td>
                                    <td><%= p.getNome() %></td>
                                    <td style="color: #ff3838; font-weight: bold;"><%= String.format("%.2f", p.getPrezzo()) %> €</td>
                                    <td><span style="background: #222; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem;"><%= p.getCategoria() %></span></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/AdminDashboard" method="post" style="display:inline;">
                                            <input type="hidden" name="azione" value="cancella">
                                            <input type="hidden" name="idPiatto" value="<%= p.getId() %>">
                                            <button type="submit" class="btn-admin-action">Elimina</button>
                                        </form>
                                    </td>
                                </tr>
                    <% 
                                    }
                                }
                            } else { 
                    %>
                            <tr>
                                <td colspan="5" style="text-align: center; color: #aaa; padding: 20px;">
                                    Nessun piatto presente nel catalogo.
                                </td>
                            </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="5" style="text-align: center; color: #aaa; padding: 20px;">
                                Caricamento del catalogo fallito o non pervenuto.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

</body>
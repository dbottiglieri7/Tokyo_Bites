<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Tokyo Bites</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>

    <%-- NAVBAR DEDICATA ALL'AMMINISTRATORE --%>
    <nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px;">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaCatalogo" style="color: white; text-decoration: none; font-weight: bold; font-size: 1.5em;">Tokyo Bites Admin 🍣</a>
        </div>
        
        <ul class="nav-links" style="list-style: none; display: flex; align-items: center; margin: 0; padding: 0;">
            <li>
                <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaCatalogo" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">
                    Gestione Catalogo 📋
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaOrdini" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">
                    Visualizza Ordini 📦
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">
                    Logout Admin 👤
                </a>
            </li>
        </ul>
    </nav>

    <main class="admin-container" style="max-width: 1100px; margin-top: 40px;">
        <h2>Console Amministratore</h2>
        
        <h3 id="form-title">Aggiungi Nuovo Prodotto nel Catalogo</h3>
        
        <form action="${pageContext.request.contextPath}/AdminDashboard" method="post" class="admin-form-inline" style="flex-wrap: wrap; gap: 15px;">
            
            <input type="hidden" id="form-azione" name="azione" value="inserisci">
            <input type="hidden" id="form-id" name="idPiatto" value="">
            
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

            <div class="form-group" style="width: 100%; min-width: 300px; margin-top: 5px;">
                <label for="descrizione">Descrizione:</label>
                <input type="text" id="descrizione" name="descrizione" placeholder="Inserisci gli ingredienti o i dettagli del piatto..." style="width: 100%;">
            </div>

            <div class="form-group" style="width: 100%; min-width: 300px; margin-top: 5px;">
                <label for="immagine">Nome File Immagine:</label>
                <input type="text" id="immagine" name="immagine" placeholder="Es. acqua.jpeg (Inserisci il file corrispondente in Eclipse)" style="width: 100%;">
            </div>
            
            <div style="width: 100%; display: flex; gap: 10px; margin-top: 10px;">
                <button type="submit" id="btn-submit" class="btn-login" style="width: auto; padding: 11px 25px;">
                    Inserisci Piatto
                </button>
                <button type="button" id="btn-annulla" class="btn-admin-action" style="display: none; background: #555;" onclick="resettaForm()">
                    Annulla Modifica
                </button>
            </div>
        </form>

        <h3>Catalogo Attuale</h3>
        
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th style="width: 6%;">ID</th>
                        <th style="width: 15%;">Nome</th>
                        <th>Descrizione</th>
                        <th style="width: 12%;">Prezzo</th>
                        <th style="width: 15%;">Categoria</th>
                        <th style="width: 15%;">Immagine</th>
                        <th style="width: 18%;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        Object catalogoObj = request.getAttribute("catalogo");
                        if (catalogoObj instanceof java.util.List) {
                            java.util.List<?> catalogo = (java.util.List<?>) catalogoObj;
                            if (!catalogo.isEmpty()) {
                                for (Object obj : catalogo) {
                                    if (obj instanceof model.Piatto) {
                                        model.Piatto p = (model.Piatto) obj;
                                        String descStr = p.getDescrizione() != null ? p.getDescrizione() : "";
                                        String imgStr = p.getImmagine() != null ? p.getImmagine() : "";
                    %>
                                <tr>
                                    <td><strong><%= p.getId() %></strong></td>
                                    <td><%= p.getNome() %></td>
                                    <td style="color: #ccc; font-size: 0.9rem; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= descStr %></td>
                                    <td style="color: #ff3838; font-weight: bold;"><%= String.format("%.2f", p.getPrezzo()) %> €</td>
                                    <td><span style="background: #222; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem;"><%= p.getCategoria() %></span></td>
                                    <td style="font-family: monospace; font-size: 0.9rem; color: #00ffcc;"><%= imgStr %></td>
                                    <td>
                                        <button type="button" class="btn-admin-action" style="background: #ffcc00; color: #000; margin-right: 5px;" 
                                                onclick="preparaModifica('<%= p.getId() %>', '<%= p.getNome().replace("'", "\\'") %>', '<%= String.format(java.util.Locale.US, "%.2f", p.getPrezzo()) %>', '<%= p.getCategoria() %>', '<%= descStr.replace("'", "\\'") %>', '<%= imgStr.replace("'", "\\'") %>')">
                                            Modifica
                                        </button>
                                        
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
                                <td colspan="7" style="text-align: center; color: #aaa; padding: 20px;">
                                    Nessun piatto presente nel catalogo.
                                </td>
                            </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="7" style="text-align: center; color: #aaa; padding: 20px;">
                                Caricamento del catalogo fallito o non pervenuto.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

	<script src="${pageContext.request.contextPath}/scripts/Admin-Dashboard.js"></script>
</body>
</html>
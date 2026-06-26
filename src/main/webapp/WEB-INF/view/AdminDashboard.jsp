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
        
        <div class="admin-subnav">
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaCatalogo">Gestione Catalogo</a> | 
            <a href="${pageContext.request.contextPath}/AdminDashboard?azione=visualizzaOrdini">Visualizza Ordini</a> | 
            <a href="${pageContext.request.contextPath}/Logout" style="color: #ff4d4d;">Logout</a>
        </div>

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
                        <th style="width: 20%;">Nome</th>
                        <th>Descrizione</th>
                        <th style="width: 12%;">Prezzo</th>
                        <th style="width: 18%;">Categoria</th>
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
                    %>
                                <tr>
                                    <td><strong><%= p.getId() %></strong></td>
                                    <td><%= p.getNome() %></td>
                                    <td style="color: #ccc; font-size: 0.9rem; max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= descStr %></td>
                                    <td style="color: #ff3838; font-weight: bold;"><%= String.format("%.2f", p.getPrezzo()) %> €</td>
                                    <td><span style="background: #222; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem;"><%= p.getCategoria() %></span></td>
                                    <td>
                                        <button type="button" class="btn-admin-action" style="background: #ffcc00; color: #000; margin-right: 5px;" 
                                                onclick="preparaModifica('<%= p.getId() %>', '<%= p.getNome().replace("'", "\\'") %>', '<%= String.format(java.util.Locale.US, "%.2f", p.getPrezzo()) %>', '<%= p.getCategoria() %>', '<%= descStr.replace("'", "\\'") %>')">
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
                                <td colspan="6" style="text-align: center; color: #aaa; padding: 20px;">
                                    Nessun piatto presente nel catalogo.
                                </td>
                            </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="6" style="text-align: center; color: #aaa; padding: 20px;">
                                Caricamento del catalogo fallito o non pervenuto.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

    <script>
        function preparaModifica(id, nome, prezzo, categoria, descrizione) {
            document.getElementById('form-title').innerText = "Modifica Prodotto (ID: " + id + ")";
            document.getElementById('form-azione').value = "modifica";
            document.getElementById('form-id').value = id;
            
            document.getElementById('nome').value = nome;
            document.getElementById('prezzo').value = prezzo;
            document.getElementById('categoria').value = categoria;
            document.getElementById('descrizione').value = descrizione;
            
            document.getElementById('btn-submit').innerText = "Salva Modifiche";
            document.getElementById('btn-submit').style.background = "#ffcc00";
            document.getElementById('btn-submit').style.color = "#000";
            document.getElementById('btn-annulla').style.display = "inline-block";
            
            // Rimanda lo scroll in alto per focalizzare il form
            window.scrollTo({top: 0, behavior: 'smooth'});
        }

        function resettaForm() {
            document.getElementById('form-title').innerText = "Aggiungi Nuovo Prodotto nel Catalogo";
            document.getElementById('form-azione').value = "inserisci";
            document.getElementById('form-id').value = "";
            
            document.getElementById('nome').value = "";
            document.getElementById('prezzo').value = "";
            document.getElementById('categoria').selectedIndex = 0;
            document.getElementById('descrizione').value = "";
            
            document.getElementById('btn-submit').innerText = "Inserisci Piatto";
            document.getElementById('btn-submit').style.background = ""; 
            document.getElementById('btn-submit').style.color = "";
            document.getElementById('btn-annulla').style.display = "none";
        }
    </script>
</body>
</html>
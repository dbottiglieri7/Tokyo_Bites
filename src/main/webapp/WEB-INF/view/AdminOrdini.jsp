<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Visualizza Ordini - Tokyo Bites</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
    <style>
        /* Semplici stili coordinati per il pop-up dei dettagli */
        .modal {
            display: none; position: fixed; z-index: 1000; left: 0; top: 0;
            width: 100%; height: 100%; background-color: rgba(0,0,0,0.8);
        }
        .modal-content {
            background-color: #1a1a1a; margin: 15% auto; padding: 20px;
            border: 1px solid #ff3838; width: 40%; border-radius: 8px; color: white;
        }
        .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
        .close:hover { color: #ff3838; }
        .select-stato {
            background: #222; color: yellow; border: 1px solid #ff3838; 
            padding: 4px 8px; border-radius: 4px; cursor: pointer;
        }
    </style>
</head>
<body>

    <main class="admin-container">
        <h2>Console Amministratore</h2>
        
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
        
        <div class="admin-table-wrapper">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th style="width: 8%;">ID</th>
                        <th>Cliente (Email)</th>
                        <th>Data Ordine</th>
                        <th>Totale Speso</th>
                        <th>Stato (Modifica con un click)</th>
                        <th>Indirizzo Spedizione</th>
                        <th>Azioni</th>
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
                                        <!-- FORM DI CAMBIO STATO AL CLICK (Gestito via AJAX asincrono) -->
                                        <form action="${pageContext.request.contextPath}/AdminDashboard" method="post" style="margin:0;">
                                            <input type="hidden" name="azione" value="modificaStato">
                                            <input type="hidden" name="idOrdine" value="<%= o.getId() %>">
                                            <select name="nuovoStato" class="select-stato" onchange="modificaStato(this)">
                                                <option value="In lavorazione" <%= "In lavorazione".equals(o.getStato()) ? "selected" : "" %>>In lavorazione</option>
                                                <option value="In consegna" <%= "In consegna".equals(o.getStato()) ? "selected" : "" %>>In consegna</option>
                                                <option value="Consegnato" <%= "Consegnato".equals(o.getStato()) ? "selected" : "" %>>Consegnato</option>
                                            </select>
                                        </form>
                                    </td>
                                    <td style="font-size: 0.85rem; color: #ccc;">
                                        <%= o.getIndirizzo() %>, <%= o.getCitta() %> (<%= o.getCap() %>)
                                    </td>
                                    <td>
                                        <!-- BOTTONE MOSTRA DETTAGLI -->
                                        <button type="button" class="btn-login" style="width:auto; padding: 5px 10px; font-size: 0.8rem; margin:0; cursor:pointer;" 
                                                onclick="mostraDettagli(event, <%= o.getId() %>)">
                                            Mostra Dettagli
                                        </button>
                                    </td>
                                </tr>
                    <% 
                                    }
                                }
                            } else { 
                    %>
                            <tr>
                                <td colspan="7" style="text-align: center; color: #aaa; padding: 20px;">
                                    Nessun ordine trovato con i filtri selezionati.
                                </td>
                            </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="7" style="text-align: center; color: #aaa; padding: 20px;">
                                Caricamento degli ordini fallito o non pervenuto.
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

    <!-- POP-UP MODAL PER DETTAGLI ORDINE -->
    <div id="dettagliModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="chiudiModal()">&times;</span>
            <h3 style="color: #ff3838; border-bottom: 1px solid #333; padding-bottom: 10px;">Dettaglio Piatti Ordine <span id="modalIdOrdine"></span></h3>
            <ul id="listaPiatti" style="padding-left: 20px; line-height: 2rem;">
                <!-- Caricato via JS -->
            </ul>
        </div>
    </div>

    <!-- LOGICA JAVASCRIPT / AJAX -->
    <script>
        // Funzione asincrona per aggiornare lo stato senza ricaricare la pagina
        function modificaStato(select) {
            var form = select.form;
            
            var dati = "azione=" + encodeURIComponent(form.azione.value) +
                       "&idOrdine=" + encodeURIComponent(form.idOrdine.value) +
                       "&nuovoStato=" + encodeURIComponent(select.value);
            
            var xhr = new XMLHttpRequest();
            xhr.open("POST", form.action, true);
            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        console.log("Stato aggiornato con successo.");
                        // Opzionale: un piccolo feedback visivo nei log del browser
                    } else {
                        alert("Errore durante l'aggiornamento dello stato.");
                    }
                }
            };
            
            xhr.send(dati);
        }

        // Funzione per mostrare i dettagli dell'ordine nel Pop-up modal
        function mostraDettagli(event, idOrdine) {
            if (event) event.preventDefault();

            document.getElementById("modalIdOrdine").innerText = "#" + idOrdine;
            var lista = document.getElementById("listaPiatti");
            lista.innerHTML = "<li>Caricamento in corso...</li>";
            
            document.getElementById("dettagliModal").style.display = "block";
            
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "${pageContext.request.contextPath}/AdminDashboard?azione=dettaglioOrdine&idOrdine=" + idOrdine, true);
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        try {
                            var piatti = JSON.parse(xhr.responseText);
                            lista.innerHTML = "";
                            
                            if (piatti.length === 0) {
                                lista.innerHTML = "<li>Nessun dettaglio trovato per questo ordine.</li>";
                            } else {
                                piatti.forEach(function(piatto) {
                                    var li = document.createElement("li");
                                    li.innerHTML = "🍣 <strong>" + piatto.nome + "</strong> - <span style='color: #ff3838;'>" + Number(piatto.prezzo).toFixed(2) + " €</span>";
                                    lista.appendChild(li);
                                });
                            }
                        } catch (e) {
                            lista.innerHTML = "<li style='color:red;'>Errore nella lettura dei dati (JSON non valido).</li>";
                        }
                    } else {
                        lista.innerHTML = "<li style='color:red;'>Errore server: Stato " + xhr.status + "</li>";
                    }
                }
            };
            xhr.send();
        }

        function chiudiModal() {
            document.getElementById("dettagliModal").style.display = "none";
        }

        window.onclick = function(event) {
            var modal = document.getElementById("dettagliModal");
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }
    </script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Login & Registrazione</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>

    <nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px;">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; font-weight: bold; font-size: 1.5em;">Tokyo Bites 🍣</a>
        </div>
        
        <ul class="nav-links" style="list-style: none; display: flex; align-items: center; margin: 0; padding: 0;">
            <li><a href="${pageContext.request.contextPath}/Home" style="color: white; text-decoration: none; margin-left: 20px;">Home 🏠</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu" style="color: white; text-decoration: none; margin-left: 20px;">Menu 🍣</a></li>
            <li><a href="${pageContext.request.contextPath}/Carrello" style="color: white; text-decoration: none; margin-left: 20px;">Carrello 🛒</a></li>
            
            <% 
                String utente = (String) session.getAttribute("utenteLoggato");
                if (utente != null) { 
            %>
                <li><a href="${pageContext.request.contextPath}/StoricoOrdini" style="color: yellow; text-decoration: none; margin-left: 20px; font-weight: bold;">Miei Ordini 📜</a></li>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Logout (<%= utente %>) 👤</a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/Login" style="color: #ff3838; text-decoration: none; margin-left: 20px; font-weight: bold;">Login 👤</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="login-page-wrapper">
    
        <div class="auth-container" style="display: flex; gap: 40px; justify-content: center; margin-top: 40px;">
            
            <div class="login-box">
                <h2>Accedi al tuo Account</h2>
                
                <% if (request.getAttribute("erroreLogin") != null) { %>
                    <div class="messaggio-errore">
                        <%= request.getAttribute("erroreLogin") %>
                    </div>
                <% } %>
                
                <form action="${pageContext.request.contextPath}/Login" method="POST">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required placeholder="Inserisci la tua email" value="${param.email}">
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required placeholder="Inserisci la tua password">
                    </div>
                    
                    <button type="submit" class="btn-login">Entra</button>
                </form>
            </div>

            <div class="login-box">
                <h2>Crea un nuovo Account</h2>
                
                <% if (request.getAttribute("messaggio") != null) { %>
                    <div class="messaggio-errore" style="background-color: rgba(46, 204, 113, 0.15); color: #2ecc71; border-color: #2ecc71;">
                        <%= request.getAttribute("messaggio") %>
                    </div>
                <% } %>
                <% if (request.getAttribute("errore") != null) { %>
                    <div class="messaggio-errore">
                        <%= request.getAttribute("errore") %>
                    </div>
                <% } %>
                
                <form action="${pageContext.request.contextPath}/RegistrazioneServlet" method="POST">
                    <div class="form-group">
                        <label for="reg_nome">Nome</label>
                        <input type="text" id="reg_nome" name="nome" required placeholder="Il tuo nome">
                    </div>
                    
                    <div class="form-group">
                        <label for="reg_cognome">Cognome</label>
                        <input type="text" id="reg_cognome" name="cognome" required placeholder="Il tuo cognome">
                    </div>

                    <div class="form-group">
                        <label for="reg_email">Email</label>
                        <input type="email" id="reg_email" name="email" required placeholder="La tua email">
                    </div>
                    
                    <div class="form-group">
                        <label for="reg_password">Password</label>
                        <input type="password" id="reg_password" name="password" required placeholder="Scegli una password">
                    </div>
                    
                    <button type="submit" class="btn-login" style="background-color: #2ecc71;">Registrati</button>
                </form>
            </div>

        </div>
    </div>

</body>
</html>
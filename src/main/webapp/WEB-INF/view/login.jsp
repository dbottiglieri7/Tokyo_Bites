<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Login & Registrazione</title>
   <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>

    <nav class="navbar">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home">Tokyo Bites 🍣</a>
        </div>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/Home">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/Menu">Menu</a></li>
            <li><a href="${pageContext.request.contextPath}/Carrello">Carrello</a></li>
            
            <% if (session.getAttribute("utenteLoggato") == null) { %>
                <li><a href="${pageContext.request.contextPath}/Login" class="active">Login</a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838;">Logout (<%= session.getAttribute("utenteLoggato") %>)</a></li>
            <% } %>
        </ul>
    </nav>

    <div class="login-page-wrapper">
    
        <div class="auth-container" style="display: flex; gap: 40px; justify-content: center; margin-top: 20px;">
            
            <div class="login-box">
                <h2>Accedi al tuo Account</h2>
                
                <%-- L'errore del login adesso compare qui dentro, perfettamente formattato --%>
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
                
                <%-- Gli errori o successi della registrazione compaiono qui dentro --%>
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
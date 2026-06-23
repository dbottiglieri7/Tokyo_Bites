<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tokyo Bites - Login</title>
   <link rel="stylesheet" type="text/css" href="/Tokyo_Bites/styles/style.css">
</head>
<body>

    <!-- Navbar coerente con la Home -->
    <nav class="navbar">
        <div class="nav-logo">
            <a href="${pageContext.request.contextPath}/Home">Tokyo Bites 🍣</a>
        </div>
        <ul class="nav-links">
    <li><a href="${pageContext.request.contextPath}/Home">Home</a></li>
    <li><a href="${pageContext.request.contextPath}/Menu">Menu</a></li>
    <li><a href="${pageContext.request.contextPath}/Carrello">Carrello</a></li>
    
    <%-- Controllo dinamico della sessione --%>
    <% if (session.getAttribute("utenteLoggato") == null) { %>
        <li><a href="${pageContext.request.contextPath}/Login">Login</a></li>
    <% } else { %>
        <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838;">Logout (<%= session.getAttribute("utenteLoggato") %>)</a></li>
    <% } %>
</ul>
    </nav>

    <!-- Contenitore del modulo di Login -->
    <div class="login-page-wrapper">
        <div class="login-box">
            <h2>Accedi al tuo Account</h2>
            
            <form action="${pageContext.request.contextPath}/Login" method="POST">
                <div class="form-group">
                    <label for="username">Username o Email</label>
                    <input type="text" id="username" name="username" required placeholder="Inserisci il tuo username">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Inserisci la tua password">
                </div>
                
                <button type="submit" class="btn-login">Entra</button>
            </form>
        </div>
    </div>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head> 
<meta charset="UTF-8">
<title>Tokyo Bites</title>
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
    
    <%-- Controllo dinamico della sessione --%>
    <% if (session.getAttribute("utenteLoggato") == null) { %>
        <li><a href="${pageContext.request.contextPath}/Login">Login</a></li>
    <% } else { %>
        <li><a href="${pageContext.request.contextPath}/Logout" style="color: #ff3838;">Logout (<%= session.getAttribute("utenteLoggato") %>)</a></li>
    <% } %>
</ul>
    </nav>

    <div class="content">
        <h1>Benvenuto su Tokyo Bites!</h1>
    </div>

</body>
</html>
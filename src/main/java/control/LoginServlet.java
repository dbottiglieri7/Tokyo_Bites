package control; // Controlla che il nome del package sia uguale al tuo attuale

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Gestisce la richiesta di visualizzazione della pagina
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

 
 // Gestisce l'invio dei dati dal form di login
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //(Placeholder per il futuro DB)
        if ("admin".equals(username) && "1234".equals(password)) {
            
            // Creiamo/Prendiamo la sessione dell'utente
            jakarta.servlet.http.HttpSession session = request.getSession(true);
            
           //Salviamo il nome utente dentro la sessione con la chiave concordata
            session.setAttribute("utenteLoggato", username);

            // Se le credenziali sono corrette, reindirizza alla Home
            response.sendRedirect(request.getContextPath() + "/Home");
        } else {
            // Altrimenti, per ora ricarica la pagina di login
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }
    
}




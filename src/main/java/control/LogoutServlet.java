package control;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupera la sessione corrente, senza crearne una nuova se non esiste (false)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            /* Invalidando la sessione si distruggono tutti gli attributi,
            compreso il 'sessionToken' e l'oggetto 'utenteLoggato', garantendo la chiusura sicura dell'accesso. */
        	
            session.invalidate(); 
        }
        
        // Pattern Post-Redirect-Get: reindirizza alla Home evitando il ricaricamento di richieste precedenti
        response.sendRedirect(request.getContextPath() + "/Home");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Mappiamo anche eventuali richieste POST sul metodo doGet per centralizzare la logica di sottomissione
        doGet(request, response);
    }
}
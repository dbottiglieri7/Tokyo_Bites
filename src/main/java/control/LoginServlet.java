package control;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UtenteDAO;
import model.Utente;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UtenteDAO utenteDAO = new UtenteDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validazione base lato server
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("erroreLogin", "Inserisci sia l'email che la password.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        Utente utente = utenteDAO.login(email, password);

        if (utente != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("utenteLoggato", utente.getNome());
            session.setAttribute("utenteCompleto", utente);
            response.sendRedirect(request.getContextPath() + "/Home");
        } else {
            // Il messaggio viene settato qui
            request.setAttribute("erroreLogin", "Email o password errate.");
            // Forward mantiene l'attributo attivo per la pagina di destinazione
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }
}
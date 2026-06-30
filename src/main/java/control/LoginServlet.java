package control;

import java.io.IOException;
import java.util.UUID;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        //Recupero del  DataSource dal contesto globale (configurato dal MainContext)
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        // 2. Istanziato il DAO passandogli il DataSource appena preso
        UtenteDAO utenteDAO = new UtenteDAO(ds);
        
        // Validazione lato server
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute("erroreLogin",
                    "Inserisci sia l'email che la password.");

            request.getRequestDispatcher("/WEB-INF/view/login.jsp")
                   .forward(request, response);
            return;
        }

        Utente utente = utenteDAO.login(email.trim(), password);

        if (utente != null) {

            // Crea una nuova sessione
            HttpSession session = request.getSession(true);

            // Timeout di 30 minuti
            session.setMaxInactiveInterval(30 * 60);

            // Dati utente
            session.setAttribute("utenteLoggato", utente.getNome());
            session.setAttribute("utenteCompleto", utente);
            session.setAttribute("ruoloUtente", utente.getRuolo());

            // Token di sessione
            String sessionToken = UUID.randomUUID().toString();
            session.setAttribute("sessionToken", sessionToken);

            // Redirect in base al ruolo
            if ("admin".equalsIgnoreCase(utente.getRuolo())) {
                response.sendRedirect(request.getContextPath() + "/AdminDashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/Home");
            }

        } else {

            request.setAttribute("erroreLogin",
                    "Email o password errate.");

            request.getRequestDispatcher("/WEB-INF/view/login.jsp")
                   .forward(request, response);
        }
    }
}
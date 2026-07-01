package control;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet che gestisce la pagina principale del sito (Home).
 * Fa parte del layer Controller nel pattern architetturale MVC.
 *
 * Comportamento:
 * - Se l'utente loggato è un ADMIN, viene reindirizzato alla dashboard amministrativa.
 * - In tutti gli altri casi (utente non loggato o CLIENTE), viene mostrata la home page.
 *
 * Mappa l'URL: /Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public Home() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recupero della sessione esistente senza crearne una nuova (false)
        //    Se non esiste sessione, session sarà null (utente non loggato)
        HttpSession session = request.getSession(false);

        if (session != null) {
            String ruolo = (String) session.getAttribute("ruoloUtente");

            // 2. Se l'utente loggato è un amministratore, redirect alla dashboard admin.
            //ATTENZIONE: il confronto deve essere con "admin" coerente con il valore salvato nel database e nella sessione dal LoginServlet.
            if ("admin".equals(ruolo)) {
                response.sendRedirect(request.getContextPath() + "/AdminDashboard");
                return;
            }
        }

        // 3. Per utenti non loggati o CLIENTI: forward alla home page.
        //    La JSP è in WEB-INF/view per impedire l'accesso diretto tramite URL.
        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/WEB-INF/view/index.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
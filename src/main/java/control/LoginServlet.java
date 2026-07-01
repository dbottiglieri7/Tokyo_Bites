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
import model.Carrello; 

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Impedisce l'accesso diretto alla pagina indirizzando la richiesta alla cartella protetta WEB-INF/view
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // 1. Recupero del DataSource dal contesto globale (configurato dal MainContext)
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        
        // 2. Istanza del DAO per interagire con la persistenza dei dati degli utenti
        UtenteDAO utenteDAO = new UtenteDAO(ds);
        
        // 3. Validazione di sicurezza lato server (In caso JavaScript sia disabilitato o aggirato)
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            request.setAttribute("erroreLogin", "Inserisci sia l'email che la password.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        // 4. Tentativo di autenticazione tramite il pattern DAO
        Utente utente = utenteDAO.login(email.trim(), password);

        if (utente != null) {

            // 5. SALVATAGGIO CARRELLO OSPITE: Recuperiamo la vecchia sessione anonima se esiste
            HttpSession vecchiaSessione = request.getSession(false);
            Carrello carrelloTemporaneo = null;
            
            if (vecchiaSessione != null) {
                // Estraiamo il carrello che l'utente ha riempito prima del login per non perdere i prodotti
                carrelloTemporaneo = (Carrello) vecchiaSessione.getAttribute("carrello");
                // Impedisce il Session Fixation ripulendo la vecchia sessione prima di loggare l'utente
                vecchiaSessione.invalidate();
            }

            // 6. Crea una nuova sessione pulita e sicura per l'utente autenticato
            HttpSession session = request.getSession(true);

            // 7. Timeout di inattività impostato a 30 minuti
            session.setMaxInactiveInterval(30 * 60);

            // 8. RIPRISTINO DEL CARRELLO: Se c'era un carrello attivo, lo iniettiamo nella nuova sessione sicura
            if (carrelloTemporaneo != null) {
                session.setAttribute("carrello", carrelloTemporaneo);
            } else {
                // Altrimenti creiamo un carrello vuoto standard per il nuovo utente
                session.setAttribute("carrello", new Carrello());
            }

            // 9. Salvataggio dei dati dell'utente all'interno della sessione
            session.setAttribute("utenteLoggato", utente.getNome());
            session.setAttribute("utenteCompleto", utente);
            session.setAttribute("ruoloUtente", utente.getRuolo());

            // 10. Generazione del Token sicuro nella sessione per il controllo accessi
            String sessionToken = UUID.randomUUID().toString();
            session.setAttribute("sessionToken", sessionToken);

            // 11. Redirezione (Pattern Post-Redirect-Get) basata sul ruolo dell'utente
            if ("admin".equalsIgnoreCase(utente.getRuolo())) {
                response.sendRedirect(request.getContextPath() + "/AdminDashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/Home");
            }

        } else {
            // 12. Credenziali errate: rimanda al form mostrando il messaggio di errore dinamico
            request.setAttribute("erroreLogin", "Email o password errate.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }
}
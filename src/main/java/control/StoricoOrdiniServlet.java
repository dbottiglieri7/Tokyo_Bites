package control;

import dao.OrdineDAO;
import model.Utente;
import model.Ordine;
import model.Piatto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet che gestisce la visualizzazione dello storico ordini dell'utente loggato.
 * Fa parte del layer Controller nel pattern architetturale MVC.
 *
 * Accesso protetto: solo gli utenti autenticati possono visualizzare i propri ordini.
 * Il controllo avviene tramite il token di sessione 'utenteLoggato'.
 *
 * Mappa l'URL: /StoricoOrdini
 */
@WebServlet("/StoricoOrdini")
public class StoricoOrdiniServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recupero della sessione corrente e verifica del token di accesso
        //    Se l'utente non è loggato, viene rediretto alla pagina di Login
        HttpSession session = request.getSession();
        String utenteLoggato = (String) session.getAttribute("utenteLoggato");
        Utente utenteCompleto = (Utente) session.getAttribute("utenteCompleto");

        if (utenteLoggato == null || utenteCompleto == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // 2. Recupero del DataSource dal contesto globale (configurato da MainContext)
        //    e istanza del DAO con il DataSource per rispettare il pattern richiesto
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        OrdineDAO ordineDAO = new OrdineDAO(ds);

        try {
            // 3. Recupero di tutti gli ordini dell'utente loggato, ordinati per data decrescente
            List<Ordine> ordini = ordineDAO.getOrdiniByUtente(utenteCompleto.getEmail());

            if (ordini != null) {
                // 4. Per ogni ordine, recupera i piatti acquistati (con nome e prezzo storici)
                //    e li salva come attributo request con chiave univoca per ordine.
                //    I dati vengono letti dalla tabella riga_ordine (colonne nome_archiviato
                //    e prezzo_acquisto), indipendentemente da modifiche successive al catalogo.
                for (Ordine o : ordini) {
                    List<Piatto> piatti = ordineDAO.doRetrievePiattiByOrdine(o.getId());
                    request.setAttribute("piatti_ordine_" + o.getId(), piatti);
                }
            }

            // 5. Passa la lista degli ordini alla JSP tramite request attribute
            request.setAttribute("listaOrdini", ordini);

        } catch (SQLException e) {
            // Rilancia come ServletException per la gestione centralizzata degli errori
            throw new ServletException(e);
        }

        // 6. Forward alla JSP di visualizzazione (in WEB-INF/view per impedire l'accesso diretto)
        request.getRequestDispatcher("/WEB-INF/view/Storico.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
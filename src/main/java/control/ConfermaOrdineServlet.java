package control;

import model.Carrello;
import model.Utente;
import model.Ordine;
import dao.OrdineDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet("/ConfermaOrdine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrdineDAO ordineDAO = new OrdineDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String utente = (String) session.getAttribute("utenteLoggato");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String utenteLoggato = (String) session.getAttribute("utenteLoggato");
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        Utente utenteCompleto = (Utente) session.getAttribute("utenteCompleto");

        if (utenteLoggato == null || carrello == null || carrello.getElementi().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Menu");
            return;
        }

        // Recuperiamo i singoli parametri di spedizione richiesti dall' OrdineDAO
        String indirizzo = request.getParameter("indirizzo");
        String citta = request.getParameter("citta");
        String cap = request.getParameter("cap");

        // Fallback di sicurezza se i campi del form checkout fossero vuoti
        if (indirizzo == null) indirizzo = "Non specificato";
        if (citta == null) citta = "Non specificato";
        if (cap == null) cap = "00000";

        // Estraiamo l'email reale dall'utente in sessione, con un fallback di sicurezza se fosse nullo
        String emailUtente = (utenteCompleto != null) ? utenteCompleto.getEmail() : "cliente@test.com";
        double totale = carrello.getPrezzoTotale();

        // Istanziamo l'oggetto Ordine inserendo anche la data corrente
        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setTotale(totale);
        nuovoOrdine.setDataOrdine(new Date()); 
        nuovoOrdine.setStato("In lavorazione");
   
        boolean ordineSalvato = ordineDAO.salvaOrdineCompleto(nuovoOrdine, emailUtente, indirizzo, citta, cap, carrello);

        response.setContentType("text/html");
        response.getWriter().println("<body style='background:#111; color:white; text-align:center; padding-top:100px; font-family:sans-serif;'>");

        if (ordineSalvato) {
            carrello.svuota();
            response.getWriter().println("<h1 style='color:yellow;'>Grazie! Ordine effettuato con successo 🍣 Pagamento Ricevuto!</h1>");
            response.getWriter().println("<p>L'ordine è stato registrato nel database e il carrello è stato svuotato.</p>");
        } else {
            response.getWriter().println("<h1 style='color:red;'>Errore durante l'elaborazione dell'ordine.</h1>");
            response.getWriter().println("<p>Riprova più tardi o contatta l'assistenza.</p>");
        }

        response.getWriter().println("<a href='" + request.getContextPath() + "/Menu' style='color:#ff3838; text-decoration:none; font-weight:bold;'>Torna al Menu</a>");
        response.getWriter().println("</body>");
    }
}
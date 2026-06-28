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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

        // 1. Recupero dei parametri del form
        String indirizzo = request.getParameter("indirizzo");
        String citta = request.getParameter("citta");
        String cap = request.getParameter("cap");
        String inputScadenza = request.getParameter("scadenza"); // Ci aspettiamo il formato MM/AAAA dal form
        String numeroCarta = request.getParameter("numeroCarta");

        // 2. CONTROLLO DI VALIDAZIONE LATO SERVER (CAP e Carta)
        if (cap == null || !cap.matches("\\d{5}")) {
            request.setAttribute("errorePagamento", "Il CAP deve essere composto esattamente da 5 cifre numeriche.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        if (numeroCarta == null || !numeroCarta.matches("\\d{16}")) {
            request.setAttribute("errorePagamento", "Il numero della carta deve essere composto esattamente da 16 cifre numeriche.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        // 3. CONTROLLO DATA DI SCADENZA CARTA
        if (inputScadenza == null || inputScadenza.isEmpty()) {
            request.setAttribute("errorePagamento", "La data di scadenza è obbligatoria.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        try {
            // Definiamo il formato di lettura (Mese e Anno a 4 cifre)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            // Parsiamo la stringa ricevuta in un oggetto YearMonth
            YearMonth scadenzaCarta = YearMonth.parse(inputScadenza.trim(), formatter);
            // Otteniamo il mese e l'anno corrente (2026)
            YearMonth meseCorrente = YearMonth.now();

            // Se la carta scade prima del mese corrente, blocchiamo l'ordine
            if (scadenzaCarta.isBefore(meseCorrente)) {
                request.setAttribute("errorePagamento", "La carta di credito inserita è scaduta.");
                request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            // Se l'utente scrive formati strani o inserisce lettere, il parse fallisce ed entra qui
            request.setAttribute("errorePagamento", "Formato data scadenza non valido. Usa il formato MM/AAAA.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        // Fallback di sicurezza se i campi di spedizione fossero vuoti
        if (indirizzo == null || indirizzo.trim().isEmpty()) indirizzo = "Non specificato";
        if (citta == null || citta.trim().isEmpty()) citta = "Non specificato";

        // Estraiamo l'email reale dall'utente in sessione
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
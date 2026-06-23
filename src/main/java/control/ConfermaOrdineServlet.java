package control;

import model.Carrello;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/ConfermaOrdine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Il GET manda l'utente alla form di pagamento (se loggato)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String utente = (String) session.getAttribute("utenteLoggato");

        if (utente == null) {
            // Se non è loggato, lo costringiamo a fare il login
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
    }

    // Il POST riceve i dati della carta validati dal JS, svuota il carrello e conferma
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello != null) {
            // Per ora simuliamo lo svuotamento al successo dell'acquisto
            carrello.svuota();
        }

        response.setContentType("text/html");
        response.getWriter().println("<body style='background:#111; color:white; text-align:center; padding-top:100px; font-family:sans-serif;'>");
        response.getWriter().println("<h1 style='color:yellow;'>Grazie! Ordine effettuato con successo 🍣 Pagamento Ricevuto!</h1>");
        response.getWriter().println("<p>Il carrello è stato svuotato.</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/Menu' style='color:#ff3838; text-decoration:none; font-weight:bold;'>Torna al Menu</a>");
        response.getWriter().println("</body>");
    }
}
package control;

import dao.OrdineDAO;
import model.Utente;
import model.Ordine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/StoricoOrdini")
public class StoricoOrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrdineDAO ordineDAO = new OrdineDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String utenteLoggato = (String) session.getAttribute("utenteLoggato");
        Utente utenteCompleto = (Utente) session.getAttribute("utenteCompleto");

        // Controllo degli accessi (Requisito Token/Sessione del professore)
        if (utenteLoggato == null || utenteCompleto == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // Recuperiamo gli ordini dal database tramite l'email dell'utente
        List<Ordine> ordini = ordineDAO.getOrdiniByUtente(utenteCompleto.getEmail());
        
        // Passiamo la lista alla JSP tramite la request
        request.setAttribute("listaOrdini", ordini);
        
        // Inoltro alla vista nascosta in WEB-INF (Requisito del professore)
        request.getRequestDispatcher("/WEB-INF/view/Storico.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
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

@WebServlet("/StoricoOrdini")
public class StoricoOrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrdineDAO ordineDAO = new OrdineDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String utenteLoggato = (String) session.getAttribute("utenteLoggato");
        Utente utenteCompleto = (Utente) session.getAttribute("utenteCompleto");

        if (utenteLoggato == null || utenteCompleto == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            List<Ordine> ordini = ordineDAO.getOrdiniByUtente(utenteCompleto.getEmail());
            
            if (ordini != null) {
                for (Ordine o : ordini) {
                    List<Piatto> piatti = ordineDAO.doRetrievePiattiByOrdine(o.getId());
                    request.setAttribute("piatti_ordine_" + o.getId(), piatti);
                }
            }
            
            request.setAttribute("listaOrdini", ordini);
            
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        
        request.getRequestDispatcher("/WEB-INF/view/Storico.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
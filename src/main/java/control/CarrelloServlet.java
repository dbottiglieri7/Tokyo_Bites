package control;

import model.Carrello;
import model.Piatto;
import dao.PiattoDAO; // Assicurati di importarlo!

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }
        request.getRequestDispatcher("/WEB-INF/view/carrello.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String azione = request.getParameter("azione");

        if (azione != null && azione.equals("rimuovi")) {
            int idPiatto = Integer.parseInt(request.getParameter("idPiatto"));
            carrello.rimuoviPiatto(idPiatto);
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else if (azione != null && azione.equals("svuota")) {
            carrello.svuota();
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else {
            // AGGIUNGI AL CARRELLO usando il database!
            int idPiatto = Integer.parseInt(request.getParameter("idPiatto"));
            PiattoDAO dao = new PiattoDAO();
            try {
                Piatto piatto = dao.doRetrieveByKey(idPiatto);
                if (piatto != null) {
                    carrello.aggiungiPiatto(piatto);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            String categoria = request.getParameter("categoriaProvenienza");
            response.sendRedirect(request.getContextPath() + "/Menu?categoria=" + (categoria != null ? categoria : "Bevande"));
        }
    }
}
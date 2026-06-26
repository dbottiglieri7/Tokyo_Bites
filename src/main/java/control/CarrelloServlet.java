package control;

import model.Carrello;
import model.Piatto;
import dao.PiattoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
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
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (azione != null && azione.equals("rimuovi")) {
            int idPiatto = Integer.parseInt(request.getParameter("idPiatto"));
            carrello.rimuoviPiatto(idPiatto);
            
            if (isAjax) {
                inviaRispostaJson(response, carrello, idPiatto);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else if (azione != null && azione.equals("svuota")) {
            carrello.svuota();
            if (isAjax) {
                inviaRispostaJson(response, carrello, -1);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else {
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
            
            if (isAjax) {
                inviaRispostaJson(response, carrello, idPiatto);
                return;
            }
            
            String categoria = request.getParameter("categoriaProvenienza");
            response.sendRedirect(request.getContextPath() + "/Menu?categoria=" + (categoria != null ? categoria : "Bevande"));
        }
    }

    // Metodo helper per generare JSON pulito a mano senza dipendenze esterne
    private void inviaRispostaJson(HttpServletResponse response, Carrello carrello, int idPiatto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        int nuovaQuantita = 0;
        double prezzoParziale = 0.0;
        
        for (Piatto p : carrello.getElementi()) {
            if (p.getId() == idPiatto) {
                nuovaQuantita++;
                prezzoParziale = p.getPrezzo() * nuovaQuantita;
            }
        }

        PrintWriter out = response.getWriter();
        out.print("{");
        out.print("\"success\": true,");
        out.print("\"carrelloVuoto\": " + carrello.getElementi().isEmpty() + ",");
        out.print("\"idPiatto\": " + idPiatto + ",");
        out.print("\"nuovaQuantita\": " + nuovaQuantita + ",");
        out.print("\"prezzoParziale\": \"" + String.format(java.util.Locale.US, "%.2f", prezzoParziale) + "\",");
        out.print("\"prezzoTotale\": \" " + String.format(java.util.Locale.US, "%.2f", carrello.getPrezzoTotale()) + "\"");
        out.print("}");
        out.flush();
    }
}
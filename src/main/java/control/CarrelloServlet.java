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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        
        //Inizializzazione automatica del carrello in sessione se non ancora esistente
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }
        
        // Inoltro protetto verso la vista del carrello
        request.getRequestDispatcher("/WEB-INF/view/carrello.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String azione = request.getParameter("azione");
        
        // Rilevamento della natura della richiesta (Se effettuata in background tramite AJAX)
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || 
                         (request.getParameter("ajax") != null);

        try {
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
                // Azione di aggiunta prodotto al carrello
                String idPiattoStr = request.getParameter("idPiatto");
                if (idPiattoStr != null && !idPiattoStr.isEmpty()) {
                    int idPiatto = Integer.parseInt(idPiattoStr);
                    PiattoDAO dao = new PiattoDAO(ds);
                    
                    Piatto piatto = dao.doRetrieveByKey(idPiatto);
                    if (piatto != null) {
                        carrello.aggiungiPiatto(piatto);
                    }
                    
                    if (isAjax) {
                        inviaRispostaJson(response, carrello, idPiatto);
                        return;
                    }
                }
                
                // Messa in sicurezza dell'URL tramite encoding per evitare che spazi o caratteri speciali rompano il redirect
                String categoria = request.getParameter("categoriaProvenienza");
                if (categoria == null || categoria.trim().isEmpty()) {
                    categoria = "Sushi e Sashimi";
                }
                String categoriaEncoded = URLEncoder.encode(categoria.trim(), StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/Menu?categoria=" + categoriaEncoded);
            }
        } catch (NumberFormatException e) {
            // Se l'ID del piatto non è valido o manipolato, rimanda al carrello senza mandare in crash l'applicazione
            response.sendRedirect(request.getContextPath() + "/Carrello");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // Generazione manuale della stringa JSON per rispondere alle chiamate AJAX del client
    private void inviaRispostaJson(HttpServletResponse response, Carrello carrello, int idPiatto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        int nuovaQuantita = 0;
        double prezzoParziale = 0.0;
        
        // Calcola la quantità aggiornata e il subtotale del prodotto specifico modificato nel carrello
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
        out.print("\"prezzoTotale\": \"" + String.format(java.util.Locale.US, "%.2f", carrello.getPrezzoTotale()) + "\"");
        out.print("}");
        out.flush();
    }
}
package control;

import dao.PiattoDAO;
import dao.OrdineDAO;
import model.Piatto;
import model.Ordine;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/AdminDashboard")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PiattoDAO piattoDAO = new PiattoDAO();
    private OrdineDAO ordineDAO = new OrdineDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // 1. Controllo Accessi
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || !ruolo.equals("admin")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            return;
        }

        String azione = request.getParameter("azione");
        if (azione == null) azione = "visualizzaCatalogo";

        try {
            switch (azione) {
                case "visualizzaCatalogo":
                    // CORRETTO: Adesso chiama getAllPiatti() che esiste nel tuo PiattoDAO
                    List<Piatto> catalogo = piattoDAO.getAllPiatti();
                    request.setAttribute("catalogo", catalogo);
                    request.getRequestDispatcher("/WEB-INF/view/AdminDashboard.jsp").forward(request, response);
                    break;

                case "visualizzaOrdini":
                    String dataInizio = request.getParameter("dataInizio");
                    String dataFine = request.getParameter("dataFine");
                    String emailCliente = request.getParameter("emailCliente");

                    List<Ordine> ordini;

                    // 1. CASO COMPLETO: Sono presenti SIA le date (almeno una) SIA l'email
                    if (((dataInizio != null && !dataInizio.isEmpty()) || (dataFine != null && !dataFine.isEmpty())) 
                            && (emailCliente != null && !emailCliente.isEmpty())) {
                        
                        if (dataInizio == null || dataInizio.isEmpty()) dataInizio = "2000-01-01";
                        if (dataFine == null || dataFine.isEmpty()) dataFine = "2100-12-31";
                        
                        // Serve un metodo combinato nel DAO (lo creiamo subito sotto)
                        ordini = ordineDAO.doRetrieveByDateAndCliente(dataInizio, dataFine, emailCliente);

                    // 2. CASO SOLO DATE: Almeno una data inserita, ma nessuna email
                    } else if ((dataInizio != null && !dataInizio.isEmpty()) || (dataFine != null && !dataFine.isEmpty())) {
                        
                        if (dataInizio == null || dataInizio.isEmpty()) dataInizio = "2000-01-01";
                        if (dataFine == null || dataFine.isEmpty()) dataFine = "2100-12-31";
                        
                        ordini = ordineDAO.doRetrieveByDate(dataInizio, dataFine);

                    // 3. CASO SOLO EMAIL: Nessuna data inserita, ma c'è l'email
                    } else if (emailCliente != null && !emailCliente.isEmpty()) {
                        ordini = ordineDAO.doRetrieveByCliente(emailCliente);

                    // 4. CASO NESSUN FILTRO: Tutto vuoto, mostra tutto l'elenco
                    } else {
                        ordini = ordineDAO.doRetrieveAllOrdini();
                    }
                    
                    request.setAttribute("ordini", ordini);
                    request.getRequestDispatcher("/WEB-INF/view/AdminOrdini.jsp").forward(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || !ruolo.equals("admin")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String azione = request.getParameter("azione");

        try {
            if ("inserisci".equals(azione)) {
                String nome = request.getParameter("nome");
                double prezzo = Double.parseDouble(request.getParameter("prezzo"));
                String categoria = request.getParameter("categoria");
                
                Piatto nuovoPiatto = new Piatto();
                nuovoPiatto.setNome(nome);
                nuovoPiatto.setPrezzo(prezzo);
                nuovoPiatto.setCategoria(categoria);
                
                piattoDAO.doSave(nuovoPiatto);
                
            } else if ("modifica".equals(azione)) {
                int id = Integer.parseInt(request.getParameter("idPiatto"));
                String nome = request.getParameter("nome");
                double prezzo = Double.parseDouble(request.getParameter("prezzo"));
                String categoria = request.getParameter("categoria");
                
                Piatto piattoModificato = new Piatto();
                piattoModificato.setId(id);
                piattoModificato.setNome(nome);
                piattoModificato.setPrezzo(prezzo);
                piattoModificato.setCategoria(categoria);
                
                piattoDAO.doUpdate(piattoModificato);

            } else if ("cancella".equals(azione)) {
                int id = Integer.parseInt(request.getParameter("idPiatto"));
                piattoDAO.doDeleteLogico(id); 
            }
            
            response.sendRedirect(request.getContextPath() + "/AdminDashboard?azione=visualizzaCatalogo");
            
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
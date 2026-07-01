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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/AdminDashboard")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // 1. Controllo Accessi Rigido (Ruolo + Controllo presenza Token)
        String ruolo = (String) session.getAttribute("ruoloUtente");
        String sessionToken = (String) session.getAttribute("sessionToken");
        
        if (ruolo == null || !ruolo.equals("admin") || sessionToken == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            return;
        }
        
        // Recupero del DataSource dal contesto globale (configurato dal MainContext)
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        // Istanziamo i DAO passando il DataSource preso dal contesto
        PiattoDAO piattoDAO = new PiattoDAO(ds);
        OrdineDAO ordineDAO = new OrdineDAO(ds);

        String azione = request.getParameter("azione");
        if (azione == null) azione = "visualizzaCatalogo";

        try {
            switch (azione) {
                case "visualizzaCatalogo":
                    List<Piatto> catalogo = piattoDAO.getAllPiatti();
                    request.setAttribute("catalogo", catalogo);
                    // Rispetta la checklist: accesso diretto impedito, le JSP sono sotto WEB-INF/view/
                    request.getRequestDispatcher("/WEB-INF/view/AdminDashboard.jsp").forward(request, response);
                    break;

                case "visualizzaOrdini":
                    String dataInizio = request.getParameter("dataInizio");
                    String dataFine = request.getParameter("dataFine");
                    String emailCliente = request.getParameter("emailCliente");

                    List<Ordine> ordini;

                    // Logica di filtraggio delle date e dei clienti per la visualizzazione degli ordini
                    if (((dataInizio != null && !dataInizio.isEmpty()) || (dataFine != null && !dataFine.isEmpty())) 
                            && (emailCliente != null && !emailCliente.isEmpty())) {
                        
                        if (dataInizio == null || dataInizio.isEmpty()) dataInizio = "2000-01-01";
                        if (dataFine == null || dataFine.isEmpty()) dataFine = "2100-12-31";
                        
                        ordini = ordineDAO.doRetrieveByDateAndCliente(dataInizio, dataFine, emailCliente);

                    } else if ((dataInizio != null && !dataInizio.isEmpty()) || (dataFine != null && !dataFine.isEmpty())) {
                        
                        if (dataInizio == null || dataInizio.isEmpty()) dataInizio = "2000-01-01";
                        if (dataFine == null || dataFine.isEmpty()) dataFine = "2100-12-31";
                        
                        ordini = ordineDAO.doRetrieveByDate(dataInizio, dataFine);

                    } else if (emailCliente != null && !emailCliente.isEmpty()) {
                        ordini = ordineDAO.doRetrieveByCliente(emailCliente);

                    } else {
                        ordini = ordineDAO.doRetrieveAllOrdini();
                    }
                    
                    request.setAttribute("ordini", ordini);
                    request.getRequestDispatcher("/WEB-INF/view/AdminOrdini.jsp").forward(request, response);
                    break;

                case "dettaglioOrdine":
                    // Chiamata AJAX per ottenere i dettagli dei piatti di un ordine senza ricaricare la pagina
                    int idOrdine = Integer.parseInt(request.getParameter("idOrdine"));
                    List<Piatto> piattiOrdinati = ordineDAO.doRetrievePiattiByOrdine(idOrdine);
                    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    
                    // Costruzione manuale del JSON per evitare dipendenze esterne non incluse
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < piattiOrdinati.size(); i++) {
                        Piatto p = piattiOrdinati.get(i);
                        json.append("{")
                            .append("\"nome\":\"").append(p.getNome().replace("\"", "\\\"")).append("\",")
                            .append("\"prezzo\":").append(p.getPrezzo())
                            .append("}");
                        if (i < piattiOrdinati.size() - 1) json.append(",");
                    }
                    json.append("]");
                    
                    out.print(json.toString());
                    out.flush();
                    return; // Interrompiamo il flusso per evitare conflitti con i forward della JSP
                
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non valida.");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        String sessionToken = (String) session.getAttribute("sessionToken");
        
        // Controllo accessi anche sulle richieste POST per sicurezza di back-end
        if (ruolo == null || !ruolo.equals("admin") || sessionToken == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            return;
        }

        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        PiattoDAO piattoDAO = new PiattoDAO(ds);
        OrdineDAO ordineDAO = new OrdineDAO(ds);

        String azione = request.getParameter("azione");

        try {
            if ("inserisci".equals(azione)) {
                String nome = request.getParameter("nome");
                String prezzoStr = request.getParameter("prezzo");
                String categoria = request.getParameter("categoria");
                String descrizione = request.getParameter("descrizione");
                String immagine = request.getParameter("immagine");
                
                // Controllo robustezza parsing numerico per evitare eccezioni impreviste sul server
                double prezzo = 0.0;
                if (prezzoStr != null && !prezzoStr.isEmpty()) {
                    prezzo = Double.parseDouble(prezzoStr);
                }
                
                Piatto nuovoPiatto = new Piatto();
                nuovoPiatto.setNome(nome);
                nuovoPiatto.setPrezzo(prezzo);
                nuovoPiatto.setCategoria(categoria);
                nuovoPiatto.setDescrizione(descrizione);
                nuovoPiatto.setImmagine(immagine);
                
                piattoDAO.doSave(nuovoPiatto);
                // Usiamo redirect dopo una POST per evitare inserimenti duplicati aggiornando la pagina (Pattern Post-Redirect-Get)
                response.sendRedirect(request.getContextPath() + "/AdminDashboard?azione=visualizzaCatalogo");
                
            } else if ("modifica".equals(azione)) {
                int id = Integer.parseInt(request.getParameter("idPiatto"));
                String nome = request.getParameter("nome");
                String prezzoStr = request.getParameter("prezzo");
                String categoria = request.getParameter("categoria");
                String descrizione = request.getParameter("descrizione");
                String immagine = request.getParameter("immagine");
                
                double prezzo = 0.0;
                if (prezzoStr != null && !prezzoStr.isEmpty()) {
                    prezzo = Double.parseDouble(prezzoStr);
                }
                
                Piatto piattoModificato = new Piatto();
                piattoModificato.setId(id);
                piattoModificato.setNome(nome);
                piattoModificato.setPrezzo(prezzo);
                piattoModificato.setCategoria(categoria);
                piattoModificato.setDescrizione(descrizione);
                piattoModificato.setImmagine(immagine);
                
                piattoDAO.doUpdate(piattoModificato);
                response.sendRedirect(request.getContextPath() + "/AdminDashboard?azione=visualizzaCatalogo");

            } else if ("cancella".equals(azione)) {
                int id = Integer.parseInt(request.getParameter("idPiatto"));
                // doDeleteLogico
                piattoDAO.doDeleteLogico(id); 
                response.sendRedirect(request.getContextPath() + "/AdminDashboard?azione=visualizzaCatalogo");
                
            } else if ("modificaStato".equals(azione)) {
                int idOrdine = Integer.parseInt(request.getParameter("idOrdine"));
                String nuovoStato = request.getParameter("nuovoStato");

                ordineDAO.doUpdateStato(idOrdine, nuovoStato);

                // Risposta AJAX testuale rapida per l'aggiornamento dinamico dello stato dell'ordine
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print("OK");
                return;
            }
            
        } catch (SQLException | NumberFormatException e) {
            throw new ServletException(e);
        }
    }
}
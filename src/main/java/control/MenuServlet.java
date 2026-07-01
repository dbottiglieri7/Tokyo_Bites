package control;

import model.Piatto;
import dao.PiattoDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/Menu")
public class MenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Lettura della categoria selezionata dall'utente via URL
        String categoriaSelezionata = request.getParameter("categoria");
        
        // Recupero del DataSource centralizzato dal contesto globale dell'applicazione
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        PiattoDAO piattoDAO = new PiattoDAO(ds);
        
        // 2.Se il parametro è vuoto o nullo, impostiamo una categoria predefinita
        if (categoriaSelezionata == null || categoriaSelezionata.trim().isEmpty()) {
            categoriaSelezionata = "Sushi e Sashimi"; 
            } else {
            categoriaSelezionata = categoriaSelezionata.trim();
        }

        // 3. Recupero dei soli piatti attivi associati alla categoria (Pattern DAO)
        List<Piatto> listaProdotti = piattoDAO.getPiattiByCategoria(categoriaSelezionata);
        
        // Messaggio di log utile in console per tracciare le transazioni dell'applicazione in tempo reale
        System.out.println("[MenuServlet] Richiesta categoria: '" + categoriaSelezionata + "' -> Trovati " + listaProdotti.size() + " prodotti.");
        
        // 4. Trasferimento dei dati raccolti dal Model alla Request per renderli accessibili alla View
        request.setAttribute("prodottiMenu", listaProdotti);
        request.setAttribute("categoriaAttuale", categoriaSelezionata);

        // Impedisce l'accesso diretto alla JSP instradandola tramite WEB-INF
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/menu.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirige eventuali richieste POST allo stesso flusso di gestione delle GET
        doGet(request, response);
    }
}
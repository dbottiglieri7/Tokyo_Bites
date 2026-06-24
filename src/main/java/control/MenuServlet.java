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
    private PiattoDAO piattoDAO = new PiattoDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Lettura della categoria dall'URL
        String categoriaSelezionata = request.getParameter("categoria");
        
        // 2. CORREZIONE LOGICA: Se è null, vuota o non valida, mostriamo una categoria ricca di default
        if (categoriaSelezionata == null || categoriaSelezionata.trim().isEmpty()) {
            categoriaSelezionata = "Sushi e Sashimi";
        } else {
            // Eliminiamo spazi bianchi superflui all'inizio e alla fine per sicurezza
            categoriaSelezionata = categoriaSelezionata.trim();
        }

        // 3. Recuperiamo i piatti dal database filtrati per la nuova categoria
        List<Piatto> listaProdotti = piattoDAO.getPiattiByCategoria(categoriaSelezionata);
        
        // Log di debug in console di Eclipse/Tomcat per verificare cosa sta succedendo in tempo reale
        System.out.println("[MenuServlet] Richiesta categoria: '" + categoriaSelezionata + "' -> Trovati " + listaProdotti.size() + " prodotti.");
        
        // 4. Invio dei dati alla pagina JSP
        request.setAttribute("prodottiMenu", listaProdotti);
        request.setAttribute("categoriaAttuale", categoriaSelezionata);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/menu.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
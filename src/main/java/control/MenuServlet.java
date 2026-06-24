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
        
        if (categoriaSelezionata == null || categoriaSelezionata.isEmpty()) {
            categoriaSelezionata = "Bevande";
        }

        // 2. Recuperiamo i piatti dal database 
        List<Piatto> listaProdotti = piattoDAO.getPiattiByCategoria(categoriaSelezionata);
        
        // 3. Invio dei dati alla pagina JSP
        request.setAttribute("prodottiMenu", listaProdotti);
        request.setAttribute("categoriaAttuale", categoriaSelezionata);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/menu.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
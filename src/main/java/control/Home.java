package control;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/", "/Home"}) //se cerco http://localhost:8080/Tokyo_Bites/ vengo portato alla home
public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public Home() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Prende la JSP dentro WEB-INF e la mostra all'utente
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/index.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Indirizza eventuali richieste in POST allo stesso metodo doGet
        doGet(request, response);
    }
}
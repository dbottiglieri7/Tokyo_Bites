package control;

import model.Piatto; // Importiamo il modello appena creato nella cartella model

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Menu")
public class MenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Leggiamo la categoria dall'URL
        String categoriaSelezionata = request.getParameter("categoria");
        
        // Default su Bevande se il parametro è vuoto
        if (categoriaSelezionata == null || categoriaSelezionata.isEmpty()) {
            categoriaSelezionata = "Bevande";
        }

        List<Piatto> listaProdotti = new ArrayList<>();
        
        // 2. Riempiamo la lista in base alla categoria selezionata
        if (categoriaSelezionata.equalsIgnoreCase("Bevande")) {
            listaProdotti.add(new Piatto(1, "Coca Cola", "In lattina da 33cl", 2.50, "Bevande", "cocacola.jpeg"));
            listaProdotti.add(new Piatto(2, "Tè Verde Giapponese", "Tè verde caldo biologico", 3.00, "Bevande", "te_verde.jpeg"));
            listaProdotti.add(new Piatto(3, "Birra Asahi", "Birra tipica giapponese da 33cl", 4.50, "Bevande", "asahi.jpeg"));
            
        } else if (categoriaSelezionata.equalsIgnoreCase("SushiRoll")) {
            listaProdotti.add(new Piatto(4, "California Roll", "Granchio, avocado, maionese e sesamo", 7.00, "SushiRoll", "california.jpeg"));
            listaProdotti.add(new Piatto(5, "Tiger Roll", "Gambero in tempura, maionese, avocado ed esternamente salmone", 10.00, "SushiRoll", "tiger.jpeg"));
            listaProdotti.add(new Piatto(6, "Philadelphia Roll", "Salmone crudo, avocado e formaggio Philadelphia", 8.50, "SushiRoll", "philadelphia.jpeg"));
            
        } else if (categoriaSelezionata.equalsIgnoreCase("Antipasti")) {
            listaProdotti.add(new Piatto(7, "Edamame", "Fagiolini di soia saltati con sale a scaglie", 4.00, "Antipasti", "edamame.jpeg"));
            listaProdotti.add(new Piatto(8, "Gyoza di Carne", "Ravioli giapponesi di carne alla piastra (5 pz)", 5.50, "Antipasti", "gyoza.jpeg"));
            listaProdotti.add(new Piatto(9, "Nuvole di Drago", "Chips croccanti al gusto di gamberetto", 3.00, "Antipasti", "nuvole.jpeg"));
            
        } else if (categoriaSelezionata.equalsIgnoreCase("Noodles")) {
            listaProdotti.add(new Piatto(10, "Yaki Udon", "Spessi noodles di grano tenero saltati con verdure e pollo", 9.50, "Noodles", "yaki_udon.jpeg"));
            listaProdotti.add(new Piatto(11, "Ramen di Maiale", "Noodles in brodo di maiale, uovo marinato, alghe e cipollotto", 12.00, "Noodles", "ramen.jpeg"));
            listaProdotti.add(new Piatto(12, "Yaki Soba", "Noodles di grano saraceno saltati con gamberi e verdure", 10.00, "Noodles", "yaki_soba.jpeg"));
        }

        // 3. Inviamo i dati alla JSP (Una sola volta, dentro il metodo doGet)
        request.setAttribute("prodottiMenu", listaProdotti);
        request.setAttribute("categoriaAttuale", categoriaSelezionata);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/menu.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
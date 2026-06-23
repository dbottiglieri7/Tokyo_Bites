package control;

import model.Carrello;
import model.Piatto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Il metodo doGet mostrerà semplicemente la pagina del carrello (la JSP che faremo dopo)
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
            // --- CASO RIMUOVI SINGOLO ---
            String idParam = request.getParameter("idPiatto");
            if (idParam != null) {
                int idPiatto = Integer.parseInt(idParam);
                carrello.rimuoviPiatto(idPiatto);
            }
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else if (azione != null && azione.equals("svuota")) {
            // --- NUOVO: CASO SVUOTA TUTTO ---
            carrello.svuota(); // Chiama il metodo svuota() che cancella l'ArrayList
            response.sendRedirect(request.getContextPath() + "/Carrello");

        } else {
            // --- CASO AGGIUNGI PRODOTTO ---
            String idParam = request.getParameter("idPiatto");
            if (idParam != null) {
                int idPiatto = Integer.parseInt(idParam);
                Piatto piattoDaAggiungere = trovaPiattoNelCatalogo(idPiatto);
                if (piattoDaAggiungere != null) {
                    carrello.aggiungiPiatto(piattoDaAggiungere);
                }
            }
            
            String categoria = request.getParameter("categoriaProvenienza");
            if (categoria == null || categoria.isEmpty()) {
                categoria = "Bevande";
            }
            response.sendRedirect(request.getContextPath() + "/Menu?categoria=" + categoria);
        }
    }

    // Funzione di supporto temporanea che simula il Database per trovare il piatto dall'ID
    private Piatto trovaPiattoNelCatalogo(int id) {
        List<Piatto> catalogo = new ArrayList<>();
        // Bevande
        catalogo.add(new Piatto(1, "Coca Cola", "In lattina da 33cl", 2.50, "Bevande", "cocacola.png"));
        catalogo.add(new Piatto(2, "Tè Verde Giapponese", "Tè verde caldo biologico", 3.00, "Bevande", "te_verde.png"));
        catalogo.add(new Piatto(3, "Birra Asahi", "Birra tipica giapponese da 33cl", 4.50, "Bevande", "asahi.png"));
        // Sushi Roll
        catalogo.add(new Piatto(4, "California Roll", "Granchio, avocado, maionese e sesamo", 7.00, "SushiRoll", "california.png"));
        catalogo.add(new Piatto(5, "Tiger Roll", "Gambero in tempura, maionese, avocado ed esternamente salmone", 10.00, "SushiRoll", "tiger.png"));
        catalogo.add(new Piatto(6, "Philadelphia Roll", "Salmone crudo, avocado e formaggio Philadelphia", 8.50, "SushiRoll", "philadelphia.png"));
        // Antipasti
        catalogo.add(new Piatto(7, "Edamame", "Fagiolini di soia saltati con sale a scaglie", 4.00, "Antipasti", "edamame.png"));
        catalogo.add(new Piatto(8, "Gyoza di Carne", "Ravioli giapponesi di carne alla piastra (5 pz)", 5.50, "Antipasti", "gyoza.png"));
        catalogo.add(new Piatto(9, "Nuvole di Drago", "Chip croccanti al gusto di gamberetto", 3.00, "Antipasti", "nuvole.png"));
        // Noodles
        catalogo.add(new Piatto(10, "Yaki Udon", "Spessi noodles di grano tenero saltati con verdure e pollo", 9.50, "Noodles", "yaki_udon.png"));
        catalogo.add(new Piatto(11, "Ramen di Maiale", "Noodles in brodo di maiale, uovo marinato, alghe e cipollotto", 12.00, "Noodles", "ramen.png"));
        catalogo.add(new Piatto(12, "Yaki Soba", "Noodles di grano saraceno saltati con gamberi e verdure", 10.00, "Noodles", "yaki_soba.png"));

        for (Piatto p : catalogo) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
package control;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.UtenteDAO;
import model.Utente;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        //Recupero del  DataSource dal contesto globale (configurato dal MainContext)
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        // 2. Istanziato il DAO passandogli il DataSource appena preso
        UtenteDAO utenteDAO = new UtenteDAO(ds);

        // Creiamo l'utente usando il costruttore vuoto + i setter
        Utente nuovoUtente = new Utente();
        nuovoUtente.setEmail(email);
        nuovoUtente.setPassword(password);
        nuovoUtente.setNome(nome);
        nuovoUtente.setCognome(cognome);
        nuovoUtente.setRuolo("CLIENTE");

        boolean registrato = utenteDAO.registraUtente(nuovoUtente);

        if (registrato) {
            System.out.println(" RegistrazioneServlet: " + email + " registrato con successo!");
            request.setAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            
            //Punta al percorso reale sotto WEB-INF 
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        } else {
            System.out.println("❌ RegistrazioneServlet: Fallito l'inserimento per " + email);
            request.setAttribute("errore", "Errore durante la registrazione. Email già esistente?");
            
            //Punta al percorso reale sotto WEB-INF
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se qualcuno prova ad accedere via GET, viene rimandato al URL del Login 
        response.sendRedirect(request.getContextPath() + "/Login");
    }
}
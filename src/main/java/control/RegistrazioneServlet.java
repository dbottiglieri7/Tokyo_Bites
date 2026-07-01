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
        
        // Recupero del DataSource dal contesto globale (configurato dal MainContext)
        javax.sql.DataSource ds = (javax.sql.DataSource) getServletContext().getAttribute("DataSource");
        // Istanziato il DAO passandogli il DataSource appena preso
        UtenteDAO utenteDAO = new UtenteDAO(ds);

        // SICUREZZA LATO SERVER: Controllo di robustezza se i dati arrivano malformati o vuoti
        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("errore", "Tutti i campi sono obbligatori.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        // Creiamo l'utente usando il costruttore vuoto + i setter (Pattern JavaBean)
        Utente nuovoUtente = new Utente();
        nuovoUtente.setEmail(email.trim());
        nuovoUtente.setPassword(password);
        nuovoUtente.setNome(nome.trim());
        nuovoUtente.setCognome(cognome.trim());
        nuovoUtente.setRuolo("CLIENTE"); // Impostazione di default per i nuovi registrati

        boolean registrato = utenteDAO.registraUtente(nuovoUtente);

        if (registrato) {
            System.out.println("✅ RegistrazioneServlet: " + email + " registrato con successo!");
            request.setAttribute("messaggio", "Registrazione completata! Ora puoi accedere.");
            
            // Passiamo il controllo alla LoginServlet 
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        } else {
            System.out.println("❌ RegistrazioneServlet: Fallito l'inserimento per " + email);
            request.setAttribute("errore", "Errore durante la registrazione. Email già esistente?");
            
            // Rimanda alla vista di login/registrazione mostrando l'errore nel DOM senza alert esterni
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se qualcuno tenta di accedere alla registrazione direttamente in GET, viene reindirizzato alla pagina di Login
        response.sendRedirect(request.getContextPath() + "/Login");
    }
}
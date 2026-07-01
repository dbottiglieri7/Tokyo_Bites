package control;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebListener
public class MainContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        ServletContext context = sce.getServletContext();
        DataSource ds = null;
        
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Qui mettiamo il DB
            ds = (DataSource) envCtx.lookup("jdbc/tokyobites");
            
        } catch (NamingException e) {
            System.out.println("Error:" + e.getMessage());
        }
        
        // Questo va fuori dal try-catch, proprio come indica la freccia nella slide!
        context.setAttribute("DataSource", ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Lasciato vuoto, esattamente come nella slide
    }
}
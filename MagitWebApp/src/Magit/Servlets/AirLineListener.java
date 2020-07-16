package Magit.Servlets;

import logic.MyAmazingGitEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.io.File;

@WebListener()
public class AirLineListener  implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    // Public constructor is required by servlet spec
    public AirLineListener() {

    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------

    public void contextInitialized(ServletContextEvent sce) {
        new File("c:/magit-ex3").mkdir();
        sce.getServletContext().setAttribute("engine", new MyAmazingGitEngine());
        sce.getServletContext().setAttribute("pullRequestID", 1);

    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("the application was shut down");
        DekelNoy3rd.Service.Methods.DeleteDirectory(new File("c:/magit-ex3"));
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {

    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}

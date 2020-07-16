package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import logic.Commit;
import logic.MyAmazingGitEngine;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PullServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        synchronized (Constants.OBJECT1){
            engine.Pull(repository);
        }

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

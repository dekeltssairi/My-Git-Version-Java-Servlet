package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

public class PushServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
        Repository repository = (Repository) request.getSession().getAttribute("repository");

        LocalBranch branchToPush = repository.GetMagit().GetActiveBranch();
        synchronized (Constants.OBJECT1){
            engine.PushBranchToRR(branchToPush, repository);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
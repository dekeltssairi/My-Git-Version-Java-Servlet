package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.MyAmazingGitEngine;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SwitchHeadServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();String branchName = request.getParameter("nameOfBranch");
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        synchronized (Constants.OBJECT1){
            repository.SwitchHeadBranchAndDeployIt(branchName);
        }


        String json = new Gson().toJson(repository);
        out.println(json);
        out.flush();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

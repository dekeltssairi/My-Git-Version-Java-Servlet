package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.MyAmazingGitEngine;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/pages/singleRepository/addbranch"})
public class AddNewBranchServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUsername(request);
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        String branchName =  request.getParameter("branchname");
        Repository repository = (Repository) request.getSession().getAttribute("repository");

        response.setContentType("application/json");
        synchronized (Constants.OBJECT2){
            backEnd.CreateNewBranchInActiveRepository(userName, branchName, repository);
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

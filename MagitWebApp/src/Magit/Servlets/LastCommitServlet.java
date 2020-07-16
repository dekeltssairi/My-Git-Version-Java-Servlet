package Magit.Servlets;

import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.Commit;
import logic.Magit;
import logic.MyAmazingGitEngine;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LastCommitServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
               response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
                String userName = SessionUtils.getUsername(request);
                String usernameToFork =  request.getParameter("userToFork");
                List<Repository> repositories;
                if(usernameToFork != null){
                    repositories = backEnd.GetUsersList().get(usernameToFork);
                } else{
                    repositories = backEnd.GetUsersList().get(userName);
                }
                String name =  request.getParameter("repositoryname");
                Repository rep = null;
                for (Repository repository: repositories){
                    if (repository.GetName().equals(name)){
                        rep = repository;
            }
        }

        String json = new Gson().toJson(rep.GetMagit().GetLastCommit());
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

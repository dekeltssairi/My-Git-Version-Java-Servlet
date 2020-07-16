package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import logic.MyAmazingGitEngine;
import logic.PullRequest;
import logic.Repository;
import logic.UserName;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public class PullRequestServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("inside pull req servlet");
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) request.getServletContext().getAttribute("engine");
        String targetName = request.getParameter("targetbranchesname");
        String baseName = request.getParameter("basebranchesname");
        String prMessage = request.getParameter("prMessage");
        String userName = SessionUtils.getUsername(request);
        Repository myRepository = (Repository) request.getSession(false).getAttribute("repository");
        Repository remoteRepository = myRepository.GetRemote().GetRemoteRepositroy();
        Path pathToRemote = Paths.get(remoteRepository.GetPath());
        UserName RRuserName = backEnd.GetUserName(pathToRemote.getParent().toFile().getName());
        int id = (int) getServletContext().getAttribute("pullRequestID");
        getServletContext().setAttribute("pullRequestID", id+1);
        UserName LRUserName = backEnd.GetUserName(userName);
        PullRequest pullRequest = new PullRequest(targetName, baseName, prMessage, LRUserName, id);
        synchronized (Constants.OBJECT2){
            remoteRepository.GetPullRequests().add(pullRequest);
        }
        String message = "You got a new pull request from " + userName+ System.lineSeparator() +
                "Repository name: " + remoteRepository.GetName() + System.lineSeparator()+
                "Base branch: "+ baseName + System.lineSeparator() +
                "Target branch: " + targetName + System.lineSeparator() +
                "Message: " + prMessage;
        RRuserName.AddMessage(message);
        response.sendRedirect("../singleRepository/singleRepository.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
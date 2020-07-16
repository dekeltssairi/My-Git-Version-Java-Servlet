package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
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

public class AcceptPullrequestServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        int pullRequestID = Integer.parseInt(request.getParameter("pullrequestID"));
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        repository.AcceptPullRequest(pullRequestID);
        PullRequest pullRequest = repository.FindPullRequest(pullRequestID);
        UserName requester = pullRequest.GetRequester();
        String message = "Your pull request with id "+pullRequest.GetID() + " to "+ repository.GetName() +" repository was accepted!" ;
        requester.AddMessage(message);

        out.println(new Gson().toJson(repository));
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

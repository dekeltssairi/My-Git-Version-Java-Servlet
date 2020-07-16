package Magit.Servlets;

import com.google.gson.Gson;
import logic.PullRequest;
import logic.Repository;
import logic.UserName;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RejectPullrequestServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int pullRequestID = Integer.parseInt(request.getParameter("pullrequestID"));
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        repository.RejectPullRequest(pullRequestID);
        String rejectMessage = request.getParameter("rejectMsg");
        PullRequest pullRequest = repository.FindPullRequest(pullRequestID);
        UserName requester = pullRequest.GetRequester();
        String message = "Your pull request with id "+pullRequest.GetID() + " to "+ repository.GetName() +" repository was rejected!" + System.lineSeparator() +
                            "Reject message: " + rejectMessage;
        requester.AddMessage(message);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

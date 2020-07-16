package Magit.Servlets;

import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CommittsListFromBaseServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Repository myRepository = (Repository) request.getSession(false).getAttribute("repository");
        int id = Integer.parseInt(request.getParameter("pullrequestID"));

        PullRequest pullRequest = myRepository.FindPullRequest(id);
        String base = pullRequest.GetBase();
        String target = pullRequest.GetTarget();
        List<Commit> committsList;
        committsList = myRepository.FindCommittsBetweenTwoBranches(base, target);
        out.println(new Gson().toJson(committsList));
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

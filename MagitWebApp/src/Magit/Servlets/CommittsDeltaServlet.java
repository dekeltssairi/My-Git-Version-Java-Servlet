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

public class CommittsDeltaServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        //MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
        Repository myRepository = (Repository) request.getSession().getAttribute("repository");
        String commitSha1 = request.getParameter("sha1");
        Commit commit = myRepository.FindCommitBySha1(commitSha1);
        Delta committsDelta = myRepository.GetDelta(commit, commit.GetParentCommit().get(0));

        out.println(new Gson().toJson(committsDelta));
        out.flush();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

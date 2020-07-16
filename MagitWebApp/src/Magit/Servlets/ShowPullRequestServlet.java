package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.PullRequest;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ShowPullRequestServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Repository repository = null;
        synchronized (Constants.OBJECT2){
             repository = (Repository) request.getSession().getAttribute("repository");
        }

        String base = request.getParameter("base");
        String target = request.getParameter("target");
        if (base != null){  // mean that we came here due to show Pull Request button
            // else its mean we came here due to show Delta
            repository.SetDelta(repository.GetDelta(base,target));
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
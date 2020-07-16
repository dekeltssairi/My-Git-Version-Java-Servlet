package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CommitServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUsername(request);
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        String commitMessage = request.getParameter("commitMsg");
        synchronized (Constants.OBJECT1){
            repository.Commit(commitMessage, new UserName(userName), null);
        }
        repository.SetAcceccibleCommitts(repository.getAccessibleCommitSorted());
        repository.SetDelta(new Delta(repository.GetPath()));
        out.println("successfully committ");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

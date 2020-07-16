package Magit.Servlets;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.MyAmazingGitEngine;
import logic.Repository;
import logic.UserName;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "SingleRepositoryServlet")
public class SingleRepositoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUsername(request);
        String repositoryName = request.getParameter("nameOfRepository");
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        Repository repository = backEnd.GetRepository(userName, repositoryName);
        UserName userNameObj = backEnd.GetUserName(userName);
        repository.SetDelta(backEnd.GetDelta(repository, userNameObj));
        request.getSession(false).setAttribute("repository", repository);

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

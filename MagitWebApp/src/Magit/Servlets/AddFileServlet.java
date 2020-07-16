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

@WebServlet(name = "AddFileServlet")
public class AddFileServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        String content = request.getParameter("content");
        String path = request.getParameter("path");
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        UserName userNameObj = backEnd.GetUserName(userName);

        Repository repository = (Repository) request.getSession().getAttribute("repository");
        repository.AddFile(userName, path, content);
        repository.SetDelta(backEnd.GetDelta(repository, userNameObj));

        PrintWriter out = response.getWriter();
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
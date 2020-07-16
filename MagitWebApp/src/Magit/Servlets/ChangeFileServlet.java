package Magit.Servlets;

import DekelNoy3rd.Service;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.MyAmazingGitEngine;
import logic.Repository;
import logic.RepositoryFile;
import logic.UserName;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ChangeFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        String userName = SessionUtils.getUsername(request);
        String path = request.getParameter("path");
        String content = request.getParameter("content");
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        repository.ChangeBlob(userName,path,content);

        UserName userNameObj = backEnd.GetUserName(userName);
        repository.SetDelta(backEnd.GetDelta(repository, userNameObj));

        String json = new Gson().toJson(repository);
        out.println(json);
        out.flush();
    }
}
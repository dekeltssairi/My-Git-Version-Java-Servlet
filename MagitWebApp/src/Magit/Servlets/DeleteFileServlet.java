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

@WebServlet(name = "Magit/Servlet/DeleteFileServlet")
public class DeleteFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        String Sha_1 = request.getParameter("sha1");
        String userName = SessionUtils.getUsername(request);
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        UserName userNameObj = backEnd.GetUserName(userName);
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        repository.DeleteFileBySha_1(Sha_1, userName);
        repository.SetDelta(backEnd.GetDelta(repository, userNameObj));

        String json = new Gson().toJson(repository);
        out.println(json);
        out.flush();
    }
}
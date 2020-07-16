package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import logic.MyAmazingGitEngine;
import logic.Repository;
import logic.UserName;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@WebServlet(name = "ForkServlet")
public class ForkServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        System.out.println("i am here in the servlet");
        String userNameToForkFrom = request.getParameter("nameOfUser");
        String repositporyName = request.getParameter("nameOfRepository");
        String userNameToForkFor = SessionUtils.getUsername(request);
        // code which to actual fork
        MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
        synchronized (Constants.OBJECT1){
                backEnd.Fork(userNameToForkFrom, repositporyName, userNameToForkFor);
        }



        UserName forkFromUserName = backEnd.GetUserName(userNameToForkFrom);
        forkFromUserName.AddMessage(userNameToForkFor + " has fork your "+ repositporyName +" repository");
        out.println("Fork is successful!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

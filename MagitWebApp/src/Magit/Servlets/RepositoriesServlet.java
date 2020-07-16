package Magit.Servlets;

import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import com.google.gson.Gson;
import logic.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(name="RepositoriesServlet")
public class RepositoriesServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
            Map<UserName, List<Repository>> users = engine.GetUsersList();
            String userNameStr;
            String usernameFromLocalStorage = request.getParameter("name");

            if (usernameFromLocalStorage != null) {
                userNameStr = usernameFromLocalStorage;
            }
            else {
                userNameStr = SessionUtils.getUsername(request);
            }
            List<Repository> repositoriesOfUser= null;
            synchronized (Constants.OBJECT1){
                repositoriesOfUser = users.get(userNameStr);
            }

            String json = new Gson().toJson(repositoriesOfUser);
            out.println(json);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

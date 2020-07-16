package Magit.Servlets;

import com.google.gson.Gson;
import logic.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class UsersListServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
            Map<UserName, List<Repository>> users = engine.GetUsersList();
            Set<UserName> usersNames = users.keySet();
            Set<String> userNamesString = new HashSet<>();
            for(UserName user : usersNames){
                if(users.get(user.GetName()).size() > 0){
                    userNamesString.add(user.GetName());
                }
            }
            String json = new Gson().toJson(userNamesString);
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

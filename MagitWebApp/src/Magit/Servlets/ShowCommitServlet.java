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
import java.util.List;
import java.util.Map;

public class ShowCommitServlet extends HttpServlet {
    //private final String COMMIT_DETAILS_URL = "../CommitDetails/CommitDetails.html";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request,response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            MyAmazingGitEngine engine = (MyAmazingGitEngine) getServletConfig().getServletContext().getAttribute("engine");
            Map<UserName, List<Repository>> users = engine.GetUsersList();
            String userName = SessionUtils.getUsername(request);
            String nameOfRepositry = request.getParameter("repositoryname");
            String Sha1 = request.getParameter("sha1");
            Repository userRepository = null;
            List<Repository> repositoriesOfUser = users.get(userName);
            for (Repository repository: repositoriesOfUser){
                if (repository.GetName().equals(nameOfRepositry))
                userRepository =repository;
            }
            Commit commit = userRepository.GetMagit().GetCommits().get(Sha1);
            RepositoryFile repositoryFile = commit.GetMainFolder();

            String json = new Gson().toJson(repositoryFile);
            out.println(json);
            out.flush();
        }
    }
}

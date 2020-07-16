package Magit.Servlets;

import logic.Commit;
import logic.MyAmazingGitEngine;
import logic.Repository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateRTBServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Repository repository = (Repository) request.getSession().getAttribute("repository");
        String branchName = request.getParameter("nameOfBranch");
        String Sha1Commit = request.getParameter("sha1CommitOfBranch");
        Commit commit = repository.FindCommitBySha1(Sha1Commit);
        repository.CreateNewBranchForSpecificCommit(branchName, commit);
        out.println("seccessfully created RTB");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

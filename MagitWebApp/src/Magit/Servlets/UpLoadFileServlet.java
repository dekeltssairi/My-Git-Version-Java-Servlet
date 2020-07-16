package Magit.Servlets;

import Generate.MagitRepository;
import Magit.constants.Constants;
import Magit.utils.SessionUtils;
import logic.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

public class UpLoadFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Collection<Part> parts = request.getParts();
        StringBuilder fileContent = new StringBuilder();
        String requestorName = SessionUtils.getUsername(request);
        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()) + " ");
            stringToDom(fileContent.toString());
            MyAmazingGitEngine backEnd = (MyAmazingGitEngine) getServletContext().getAttribute("engine");
            MagitRepository magitRepository = null;
            if(backEnd.IsPathToXMLFile(part.getName())) {
                magitRepository = JAXB.loadXML("C:/magit-ex3/my-file.xml");
                new File ("C:/magit-ex3/my-file.xml").delete();
                XMLValidation validationMessage = backEnd.CheckXMLValidation(magitRepository);
                out.println(validationMessage.getName() + System.lineSeparator());
                if (validationMessage.equals(XMLValidation.VALID_XML)) { // is valid XML file
                    if (backEnd.IsAlreadyDirectoryInSystem(magitRepository.getName(), requestorName)) { // is already directory in user directory
                        //if (backEnd.IsAlreadyRepository(magitRepository)) {
                            out.println("However, this repository name already exist in your repositories");
                        //}
                    }else{
                        backEnd.ConvertMagitRepositoryToOurRepository(magitRepository);
                        backEnd.GetActiveRepository().moveToTheCommonFolder(requestorName);
                        String pathToRepository = "c:/magit-ex3/"+requestorName+"/"+magitRepository.getName();
                        backEnd.LoadExistRepository(pathToRepository);
                        backEnd.GetActiveRepository().DeployHeadBranch(); // to check
                        synchronized (Constants.OBJECT1){
                            backEnd.GetUsersList().get(requestorName).add(backEnd.GetActiveRepository());
                        }

                        out.println("The repository " + magitRepository.getName() +" was loaded successfily!");
                    }
                }
            } else {
                out.println("This is not an XML repository file!");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fileupload/form.html");
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    public  void stringToDom(String xmlSource)
            throws IOException {
        FileWriter fw = new FileWriter("c:/magit-ex3/my-file.xml");
        fw.write(xmlSource);
        fw.close();
    }
}

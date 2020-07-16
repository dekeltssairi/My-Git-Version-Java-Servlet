package logic;

import java.io.*;
import java.nio.file.*;
import  java.util.*;
import java.util.zip.ZipFile;

import static org.apache.commons.codec.digest.DigestUtils.*;
import static DekelNoy3rd.Service.Methods.*;

public class WorkingCopy {

    private String m_Path;
    private RepositoryFile m_MainFolder;

    public WorkingCopy(String i_Path, RepositoryFile i_RepositoryFile) {
       m_Path = i_Path;
       m_MainFolder = i_RepositoryFile;
    }

    public String GetPath() {
        return m_Path;
    }

    public void GenerateSystemFiles(UserName i_UserName, Map<String,String> i_StringZipFilesMap, Map<String,RepositoryFile> i_CurrentCommitMap) {
        File mainFolder = new File(m_Path.toString());
        DeleteEmptyDirectoriesWithoutSpecificFolder(m_Path, ".magit");
        m_MainFolder = CreateSystemFilesRec(mainFolder, Paths.get(m_Path), i_UserName, i_StringZipFilesMap, i_CurrentCommitMap);
    }

    public RepositoryFile GetMainFolder() {return m_MainFolder;}

    public RepositoryFile CreateSystemFilesRec(File myFile, Path i_Path, UserName i_UserName, Map<String, String> i_StringZipFilesMap, Map<String,RepositoryFile> i_CurrentCommitMap) {
        RepositoryFile someFile = null;

        if (myFile.exists() == false) {
            someFile = null;
        }
        String fileName = myFile.getName();
        String sha_1;
        Date lastModifyDate;

        if (myFile.isFile()) {
            fileName = myFile.getName();
            String content = ReadContentOfTextFile(myFile.getPath());
            content = fixString(content);
            sha_1 = sha1Hex(content);
            if(i_StringZipFilesMap.containsKey(sha_1) && i_CurrentCommitMap != null && i_CurrentCommitMap.containsKey(sha_1)){
                //if (i_CurrentCommitMap != null && i_CurrentCommitMap.containsKey(sha_1)){
                someFile = i_CurrentCommitMap.get(sha_1);
                if (!someFile.GetPath().equals(i_Path.toString())){
                    someFile.SetPath(i_Path.toString());
                    someFile.SetCommitter(i_UserName.GetName());
                }
                //}
            }
            else {
                someFile = new Blob(fileName, sha_1, i_UserName.GetName(), new Date(myFile.lastModified()), i_Path.toString(), content);
            }
        }
        else {
            File[] subFilesArray = myFile.listFiles();
            Arrays.sort(subFilesArray);
            List<File> subFilesList = new ArrayList<File>(Arrays.asList(subFilesArray));
            removeMagitFolderFromList(subFilesList);
            List<RepositoryFile> repositoryFilesInDirectory = new ArrayList<>(subFilesList.size());
            String chainOfAllInnerSha_1 = "";
            for (int i = 0; i < subFilesList.size(); i++) {
                Path pathToSubFile = subFilesList.get(i).toPath();
                RepositoryFile subFile = CreateSystemFilesRec(subFilesList.get(i),pathToSubFile, i_UserName, i_StringZipFilesMap, i_CurrentCommitMap);
                repositoryFilesInDirectory.add(subFile);
                chainOfAllInnerSha_1 += subFile.GetSha_1();
            }
            if (repositoryFilesInDirectory.size() > 0) {
                lastModifyDate = getLastModifyDateOfFolder(repositoryFilesInDirectory);
            }
            else {
                lastModifyDate = new Date(myFile.lastModified());
            }
            sha_1 = sha1Hex(chainOfAllInnerSha_1);
            if(i_StringZipFilesMap.containsKey(sha_1) && (i_CurrentCommitMap!= null && i_CurrentCommitMap.containsKey(sha_1))) {
                //if (i_CurrentCommitMap!= null && i_CurrentCommitMap.containsKey(sha_1)){
                someFile = i_CurrentCommitMap.get(sha_1);
                if (!someFile.GetPath().equals(i_Path.toString())){
                    someFile.SetPath(i_Path.toString());
                    someFile.SetCommitter(i_UserName.GetName());
                }
                //}
            }
            else {
                someFile = new Folder(fileName, sha_1, i_UserName.GetName(), lastModifyDate, i_Path.toString(), repositoryFilesInDirectory);
            }
        }
        return someFile;
    }

    private Date getLastModifyDateOfFolder(List<RepositoryFile> repositoryFilesInDirectory) {
        Date lastDate = repositoryFilesInDirectory.get(0).GetDate();
        for (int i = 1; i < repositoryFilesInDirectory.size(); i++){
            Date date = repositoryFilesInDirectory.get(i).GetDate();
            if (lastDate.compareTo(date) < 0){
                lastDate = date;
            }
        }
        return lastDate;
    }

    private void removeMagitFolderFromList(List<File> i_ListOfFiles){
        for (int i = 0; i < i_ListOfFiles.size(); i++){
            if(i_ListOfFiles.get(i).getName().equals(".magit")){
                i_ListOfFiles.remove(i);
            }
        }
    }

    public void Clean() {
        File[] mainFolderSubFiles = new File (m_Path.toString()).listFiles();
        List<File> subFilesList = new ArrayList<File>(Arrays.asList(mainFolderSubFiles));
        removeMagitFolderFromList(subFilesList);
        for(File file : subFilesList){
            DeleteDirectory(file);
        }
    }

    public void GenerateSystemFiles(List<RepositoryFile> i_RepositoryFilesList, Path i_PathToFolder) {
        File mainFolder = new File(i_PathToFolder.toString());
        generateSystemFilesRec(mainFolder, i_RepositoryFilesList);
    }

    private void generateSystemFilesRec(File i_Folder, List<RepositoryFile> i_RepositoryFilesList) {
        for(RepositoryFile repositoryFile : i_RepositoryFilesList){
            Path pathOfParentFolder = Paths.get(repositoryFile.GetPath()).getParent();
            if(pathOfParentFolder.toString().equals(i_Folder.getAbsolutePath())){

                if(repositoryFile instanceof Blob){
                    Path pathToObjects = Paths.get(m_Path + "/.magit/Objects");
                    String pathToZip = pathToObjects.toString() + "/" + repositoryFile.GetSha_1() + ".zip";
                    String blobContent = ReadFromZip(pathToZip);
                    CreateTextFile(repositoryFile.GetPath(), blobContent);
                }

                else{
                    File folder = new File(repositoryFile.GetPath().toString());
                    folder.mkdir();
                    generateSystemFilesRec(folder, i_RepositoryFilesList);
                }
            }
        }
    }

    public boolean HasFilesInWC() {
        DeleteEmptyDirectoriesWithoutSpecificFolder(m_Path, ".magit");
        return new File(m_Path).listFiles().length > 1;
    }

    public void DeleteFileBySha_1(String sha_1) {
        Folder contiansFolder = findContainsFolder((Folder) m_MainFolder, sha_1);
        if (contiansFolder != null) {
            List<RepositoryFile> repositoryFiles = contiansFolder.GetRepositoryFiles();
            for (int i = 0; i < repositoryFiles.size(); i++) {
                if (repositoryFiles.get(i).GetSha_1().equals(sha_1)) {
                    repositoryFiles.remove(i);
                    break;
                }
            }
            if (repositoryFiles.size() == 0 && contiansFolder != m_MainFolder) {
                DeleteFileBySha_1(contiansFolder.GetSha_1());
            }
        }
        else{
            m_MainFolder = null;
        }
        DeployWC();
    }

    private void DeployWC() {
        Clean();
        DeployWcRec(m_MainFolder);
    }

    private void DeployWcRec(RepositoryFile i_RepositoryFile) {
        if (i_RepositoryFile != null){
            if (i_RepositoryFile instanceof Blob){
                ((Blob)i_RepositoryFile).CreateFile();
            }
            else{
                Folder folder = (Folder)i_RepositoryFile;
                folder.CreateFolder();
                for (RepositoryFile repositoryFile: folder.GetRepositoryFiles()){
                    DeployWcRec(repositoryFile);
                }
            }
        }
    }

    private Folder findContainsFolder(Folder m_MainFolder, String sha_1) {
        List<RepositoryFile> repositoryFiles = m_MainFolder.GetRepositoryFiles();
        Folder folder = null;
        for (RepositoryFile repositoryFile : repositoryFiles) {
            if(folder == null){
                if (repositoryFile.GetSha_1().equals(sha_1)) {
                    folder = m_MainFolder;
                } else if (repositoryFile instanceof Folder) {
                    folder =  findContainsFolder((Folder) repositoryFile, sha_1);
                }
            }
            else {
                break;
            }
        }
        return  folder;
    }

    public void ChangeBlob(String userName, String path, String content) {
        changeBlobRec(m_MainFolder, userName,path,content);
        DeployWC();
    }

    private void changeBlobRec(RepositoryFile i_RepositoryFile, String i_UserName, String i_Path, String i_Content) {
        if (i_RepositoryFile != null){
            if (i_RepositoryFile.GetPath().equals(i_Path)){
                ((Blob)i_RepositoryFile).SetContent(i_Content);
                i_RepositoryFile.SetCommitter(i_UserName);
            }
            else if (i_RepositoryFile instanceof Folder){
                Folder folder = (Folder) i_RepositoryFile;
                for (RepositoryFile repositoryFile: folder.GetRepositoryFiles()){
                    changeBlobRec(repositoryFile, i_UserName,i_Path,i_Content);
                }
            }
        }
    }

    public void SetMainFolder(RepositoryFile i_MainFolder) {
        m_MainFolder = i_MainFolder;
    }
}
package logic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static DekelNoy3rd.Service.Methods.*;

public class Remote {

    private Magit m_LocalMagit;
    private Repository m_RemoteRepositroy;
    private String m_PathToRemote;
    private String m_pathToMagitInRemote;
    private String m_PathToObjectsInRemote;
    private String m_PathToBranchesInRemote;
    private String m_PathToLocal;
    private String m_PathToMagitInLocal;
    private String m_PathToObjectsInLocal;
    private String m_PathToRtbBranches;
    private String m_PathToRemoteBranchesInLocal;

    private String m_RemoteRepsoitoryName;
    private List<RemoteBranch> m_RemoteBranches;
    private BranchLoader m_BranchLoader;

    public Remote(Path i_PathToRemote, Path i_PathToLocal, Repository i_LocalRepository) {
        m_PathToRemote = i_PathToRemote.toString();
        m_pathToMagitInRemote = Paths.get(m_PathToRemote + "/.magit").toString();
        m_PathToObjectsInRemote = Paths.get(m_pathToMagitInRemote + "/Objects").toString();
        m_PathToBranchesInRemote = Paths.get(m_pathToMagitInRemote + "/Branches").toString();
        m_PathToLocal = i_PathToLocal.toString();
        m_PathToMagitInLocal = Paths.get(m_PathToLocal + "/.magit").toString();
        m_PathToObjectsInLocal = Paths.get(m_PathToMagitInLocal + "/Objects").toString();
        m_PathToRtbBranches = Paths.get(m_PathToMagitInLocal + "/Branches").toString();
        m_RemoteRepsoitoryName = ReadContentOfTextFile(m_pathToMagitInRemote + "/Name").toString();
        m_PathToRemoteBranchesInLocal = Paths.get(m_PathToRtbBranches + "/" + m_RemoteRepsoitoryName).toString();
        //m_RemoteRepositroy = new Repository(m_RemoteRepsoitoryName, i_PathToRemote.toString());
        m_LocalMagit = i_LocalRepository.GetMagit();

        m_RemoteBranches = new ArrayList<>();
        // m_BranchLoader = m_LocalRepository.GetMagit().GetBranchLoader();
       // loadRemoteBranches(m_LocalMagit.GetCommits());
    }

//    private void loadRemoteBranches() {
//        File[] branchesTextFiles = new File(m_PathToRemoteBranchesInLocal.toString()).listFiles();
//        for (int i = 0; i < branchesTextFiles.length; i++) {
//            Path branchPath = Paths.get(branchesTextFiles[i].getAbsolutePath());
//            m_RemoteBranches.add((m_LocalRepository.GetMagit().GetBranchLoader().loadBranch(branchPath, new HashMap<>())).ToRemoteBranch());//m_BranchLoader.loadBranch(branchPath,i_Commits)).ToRemoteBranch());
//        }
//    }
public Repository GetRemoteRepositroy() {
    return m_RemoteRepositroy;
}

    public void loadRemoteBranches( Map<String, Commit> i_Commits) {
        File[] branchesTextFiles = new File(m_PathToRemoteBranchesInLocal.toString()).listFiles();
        for (int i = 0; i < branchesTextFiles.length; i++) {
            Path branchPath = Paths.get(branchesTextFiles[i].getAbsolutePath());
            String SHA1ofCommit = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(branchPath.toString());
            Commit commit = i_Commits.get(SHA1ofCommit);
            m_RemoteBranches.add((new LocalBranch(branchPath.toFile().getName(), commit, branchPath.toString()).ToRemoteBranch(m_RemoteRepositroy)));
        }
    }

//    public void Fetch() {
//        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
//        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
//            localBranch.CreateBranchTextFile(Paths.get(m_PathToRemoteBranchesInLocal));
//        }
//
//        for (Commit commit : m_RemoteRepositroy.GetMagit().GetCommits().values()){
//            commit.CreateTextFile(Paths.get(m_LocalMagit.GetPath()));
//        }
//
//        for (String pathToZip : m_RemoteRepositroy.GetMagit().GetObjects().values()){
//            try {
//                FileUtils.copyFileToDirectory(Paths.get(pathToZip).toFile(), Paths.get(m_PathToObjectsInLocal).toFile());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        File [] zipFiles = Paths.get(m_PathToObjectsInLocal).toFile().listFiles();
//        for (File zipFile: zipFiles){
//            String name = DekelNoy3rd.Service.Methods.GetZipFileName(zipFile.toPath());
//            if (name.equals(Paths.get(m_PathToRemote).toFile().getName())){
//                DekelNoy3rd.Service.Methods.Unzip(zipFile.toPath().toString(),m_PathToObjectsInLocal.toString());
//                Path pathToNewTextFile = Paths.get(m_PathToObjectsInLocal + "/" + name);
//                String mainFolderName = Paths.get(m_PathToLocal).toFile().getName();
//                Path pathToRenameFile =  Paths.get(m_PathToObjectsInLocal + "/" + mainFolderName);
//                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
//                Path pathToZipInLocal = Paths.get(m_PathToObjectsInLocal + "/" + zipFile.getName());
//                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInLocal);
//                pathToRenameFile.toFile().delete();
//            }
//        }
//    }

    public void Pull() {
        LocalBranch headBranchInLocal = m_LocalMagit.GetActiveBranch();
        LocalBranch branchInRemote = null;

        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
            if (localBranch.GetName().equals(headBranchInLocal.GetName())){
                branchInRemote = localBranch;
            }
        }
        branchInRemote.CreateBranchTextFile(Paths.get(m_PathToRemoteBranchesInLocal));
        branchInRemote.CreateBranchTextFile(Paths.get(m_PathToRtbBranches));
        pullCommitRec(branchInRemote.GetCommit());
    }

    private void pullCommitRec(Commit i_Commit) {
        boolean hasIdenticalCommit = m_LocalMagit.GetCommits().containsKey(i_Commit.GetSha1());
        if (!hasIdenticalCommit) {
            i_Commit.CreateTextFile(Paths.get(m_LocalMagit.GetPath()));
            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
            boolean isFirstRecursionInstance = true;
            pullZipFileRec(mainFolderSHA1, "Folder", isFirstRecursionInstance);
            List<Commit> parentsCommit = i_Commit.GetParentCommit();
            if (parentsCommit != null) {
                if (parentsCommit.size() > 0) {
                    pullCommitRec(parentsCommit.get(0)); /// here i was need to call to pullCommitRec ant not pushCommitRec
                }
                if (parentsCommit.size() == 2) {
                    pullCommitRec(parentsCommit.get(1));
                }
            }
        }
    }

    private void pullZipFileRec(String i_SHA1, String i_Item, boolean i_IsFirstRecursionInstance)  {

        if (!m_LocalMagit.GetObjects().containsKey(i_SHA1)){
            String pathToZipInRemote = m_RemoteRepositroy.GetMagit().GetObjects().get(i_SHA1);
            try {
                FileUtils.copyFileToDirectory(Paths.get(pathToZipInRemote).toFile(),Paths.get(m_PathToObjectsInLocal) .toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i_IsFirstRecursionInstance){
                DekelNoy3rd.Service.Methods.Unzip(pathToZipInRemote,m_PathToObjectsInLocal);
                String name = DekelNoy3rd.Service.Methods.GetZipFileName(Paths.get(pathToZipInRemote));
                Path pathToNewTextFile = Paths.get(m_PathToObjectsInLocal + "/" + name);
                String mainFolderName = Paths.get(m_PathToLocal).toFile().getName();
                Path pathToRenameFile =  Paths.get(m_PathToObjectsInLocal + "/" + mainFolderName);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                Path pathToZipInLocal = Paths.get(m_PathToObjectsInLocal + "/" + Paths.get(pathToZipInRemote).toFile().getName());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInLocal);
                pathToRenameFile.toFile().delete();
                i_IsFirstRecursionInstance = false;
            }
            if (i_Item.equals("Folder")){
                String zipContent = DekelNoy3rd.Service.Methods.ReadFromZip(pathToZipInRemote.toString());
                zipContent = fixString(zipContent);
                String[] linesOfZip = zipContent.split(System.lineSeparator());
                for (String str : linesOfZip) { // only if folder
                    String[] output = str.split(",");
                    pullZipFileRec(output[1], output[2], i_IsFirstRecursionInstance);
                }
            }
        }
    }

    public List<RemoteBranch> GetRemoteBranches() {
        return m_RemoteBranches;
    }

    public boolean IsHeadRtb() {
        boolean isHeadRtb = false;
        for (RemoteBranch remoteBranch : m_RemoteBranches){
            if (remoteBranch.GetName().equals(m_LocalMagit.GetActiveBranch().GetName())){
                isHeadRtb = true;
            }
        }
        return isHeadRtb;
    }

    public boolean IsRemoteClear() {
        //m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        return !m_RemoteRepositroy.IsSomethingToCommit();
    }

    public void Push() {
        LocalBranch localHeadBranch = m_LocalMagit.GetActiveBranch();
        localHeadBranch.CreateBranchTextFile(Paths.get(m_PathToBranchesInRemote));
        localHeadBranch.CreateBranchTextFile(Paths.get(m_PathToRemoteBranchesInLocal));
        pushCommitRec(localHeadBranch.GetCommit());
        //m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
        m_RemoteRepositroy.UpdateAfterPush();
        if (localHeadBranch.GetName().equals(m_RemoteRepositroy.GetMagit().GetActiveBranch().GetName())){
            m_RemoteRepositroy.GetWC().Clean();
            m_RemoteRepositroy.GetMagit().GetActiveBranch().Deploy();
        }
    }

//    public void CommitRec(Commit i_Commit, Repository i_repository, Method method){
//        boolean hasIdenticalCommit = i_repository.GetMagit().GetCommits().containsKey(i_Commit.GetSha1());
//        if (!hasIdenticalCommit) {
//            i_Commit.CreateTextFile(i_repository.GetMagit().GetPath());
//            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
//            boolean isFirstRecursionInstance = true;
//            try {
//                method.invoke((Object)mainFolderSHA1, (Object)"Folder", (Object)isFirstRecursionInstance);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            List<Commit> parentsCommit = i_Commit.GetParentCommit();
//            if (parentsCommit != null) {
//                if (parentsCommit.size() > 0) {
//                    CommitRec(parentsCommit.get(0), i_repository, method);
//                }
//                if (parentsCommit.size() == 2) {
//                    CommitRec(parentsCommit.get(0), i_repository, method);
//                }
//            }
//        }
//
//    }

    public void pushZipFileRec(String i_SHA1, String i_Item,  boolean i_IsFirstRecursionInstance){
        if (!m_RemoteRepositroy.GetMagit().GetObjects().containsKey(i_SHA1)){
            String pathToZipInLocal = m_LocalMagit.GetObjects().get(i_SHA1);
            try {
                FileUtils.copyFileToDirectory(Paths.get(pathToZipInLocal).toFile(), Paths.get(m_PathToObjectsInRemote).toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i_IsFirstRecursionInstance){
                DekelNoy3rd.Service.Methods.Unzip(pathToZipInLocal.toString(),m_PathToObjectsInRemote.toString());
                String name = DekelNoy3rd.Service.Methods.GetZipFileName(Paths.get(pathToZipInLocal));
                Path pathToNewTextFile = Paths.get(m_PathToObjectsInRemote + "/" + name);
                String mainFolderName = Paths.get(m_RemoteRepositroy.GetPath()).toFile().getName();
                Path pathToRenameFile =  Paths.get(m_PathToObjectsInRemote + "/" + mainFolderName);
                pathToNewTextFile.toFile().renameTo(pathToRenameFile.toFile());
                Path pathToZipInRemote = Paths.get(m_PathToObjectsInRemote + "/" + Paths.get(pathToZipInLocal).toFile().getName());
                DekelNoy3rd.Service.Methods.Zip(pathToRenameFile,pathToZipInRemote);
                pathToRenameFile.toFile().delete();
                i_IsFirstRecursionInstance = false;
            }
            if (i_Item.equals("Folder")){
                String zipContent = DekelNoy3rd.Service.Methods.ReadFromZip(pathToZipInLocal.toString());
                zipContent = fixString(zipContent);
                String[] linesOfZip = zipContent.split(System.lineSeparator());
                for (String str : linesOfZip) { // only if folder
                    String[] output = str.split(",");
                    pushZipFileRec(output[1], output[2], i_IsFirstRecursionInstance);
                }
            }
        }
    }

    public boolean IsRemoteBranchInLocalSameAsRemoteBranchInRemote() {
        String headBranchInLocalName = m_LocalMagit.GetActiveBranch().GetName();
        RemoteBranch remoteBranchInLocal = null;
        LocalBranch branchInRemote = null;
        for (RemoteBranch remoteBranch: m_RemoteBranches){
            if (remoteBranch.GetOriginName().equals(headBranchInLocalName)){
                remoteBranchInLocal = remoteBranch;
            }
        }

        for (LocalBranch localBranch: m_RemoteRepositroy.GetMagit().GetBranches()){
            if (localBranch.GetName().equals(headBranchInLocalName)){
                branchInRemote = localBranch;
            }
        }

        return (remoteBranchInLocal.GetCommit().equals(branchInRemote.GetCommit()));
    }

    public boolean IsSomeThingToPush() {
        LocalBranch headBranchRtb = m_LocalMagit.GetActiveBranch();
        RemoteBranch remoteHeadBranchInLocal =null;
        for (RemoteBranch remoteBranch: m_RemoteBranches){
            String originRBName = remoteBranch.GetOriginName();
            if (originRBName.equals(headBranchRtb.GetName())){
                remoteHeadBranchInLocal = remoteBranch;
            }
        }
        return !remoteHeadBranchInLocal.GetCommit().GetSha1().equals(headBranchRtb.GetCommit().GetSha1());
    }

//    public boolean IsHeadBranchInRemoteIsRtbInLocal() {
//        boolean IsHeadBranchInRemoteIsRtbInLocal = false;
//        m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath().toString());
//        LocalBranch headBranchInRemote = m_RemoteRepositroy.GetMagit().GetActiveBranch();
//        for (LocalBranch rtbBranch: m_LocalMagit.GetBranches()){
//            if(rtbBranch.GetName().equals(headBranchInRemote.GetName())){
//                IsHeadBranchInRemoteIsRtbInLocal = true;
//            }
//        }
//        return IsHeadBranchInRemoteIsRtbInLocal;
//    }

    public boolean isHeadBranchInLocalIsRtb() {
        boolean IsHeadBranchInLocalIsRtb = false;
        LocalBranch headBranchInLocal = m_LocalMagit.GetActiveBranch();
        for (RemoteBranch remoteBranch : m_RemoteBranches){
            String originName = remoteBranch.GetOriginName();
            if (headBranchInLocal.GetName().equals(originName)){
                IsHeadBranchInLocalIsRtb = true;
            }
        }
        return IsHeadBranchInLocalIsRtb;
    }



    private void pushCommitRec(Commit i_Commit) {
        boolean hasIdenticalCommit = m_RemoteRepositroy.GetMagit().GetCommits().containsKey(i_Commit.GetSha1());
        if (!hasIdenticalCommit) {
            i_Commit.CreateTextFile(Paths.get(m_RemoteRepositroy.GetMagit().GetPath()));
            String mainFolderSHA1 = i_Commit.GetMainFolderSha_1();
            boolean isFirstRecursionInstance = true;
            pushZipFileRec(mainFolderSHA1, "Folder", isFirstRecursionInstance);
            List<Commit> parentsCommit = i_Commit.GetParentCommit();
            if (parentsCommit != null) {
                if (parentsCommit.size() > 0) {
                    pushCommitRec(parentsCommit.get(0));
                }
                if (parentsCommit.size() == 2) {
                    pushCommitRec(parentsCommit.get(1));
                }
            }
        }
    }

    public void PushBranchToRR(LocalBranch i_branch) {
        //m_RemoteRepositroy = new Repository(m_RemoteRepositroy.GetName(), m_RemoteRepositroy.GetPath());
        i_branch.CreateBranchTextFile(Paths.get(m_PathToBranchesInRemote));
        i_branch.CreateBranchTextFile(Paths.get(m_PathToRemoteBranchesInLocal));
        pushCommitRec(i_branch.GetCommit());
        m_RemoteRepositroy.UpdateAfterPush(i_branch.GetName());
        m_LocalMagit.UpdateAfterPush();
    }

    public String GetPathToRemote() {
        return m_PathToRemote;
    }

    public void SetRemoteRepository(Repository i_RemoteRepository) {
        m_RemoteRepositroy = i_RemoteRepository;
    }
}
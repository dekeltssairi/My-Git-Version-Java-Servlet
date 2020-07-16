package logic;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import static DekelNoy3rd.Service.Methods.*;

public class Repository {
    private String m_Name;
    private String m_Path;
    boolean m_IsLocal;
    private WorkingCopy m_WC;
    private Magit m_Magit;
    private Remote m_Remote;
    private Delta m_Delta;
    private List<Commit> m_AcceccibleCommitts;
    private List<PullRequest> m_PullRequest;

    public Repository(String i_Name, String i_PathStr) {
        m_Name = i_Name;
        m_Path = Paths.get(i_PathStr).toAbsolutePath().toString();
        m_IsLocal = isLocalRepository();
        m_Magit = new Magit(i_Name, Paths.get(m_Path.toString() + "/.magit"), m_IsLocal);
        m_WC = new WorkingCopy(m_Path, GetActiveCommit().GetMainFolder());
        m_Delta = new Delta(m_Path);
        m_AcceccibleCommitts = getAccessibleCommitSorted();
        m_PullRequest = null;
        if (m_IsLocal) {
            Path path = Paths.get(m_Path + "/.magit/Remote");
            Path pathToRemote = Paths.get(DekelNoy3rd.Service.Methods.ReadContentOfTextFile(path.toString()));
            m_Remote = new Remote(pathToRemote, Paths.get(m_Path), this);
        }
    }

    public void SetPath(String i_Path) {
        m_Path = i_Path;
    }

    public void SetAcceccibleCommitts(List<Commit> i_AccessibleCommitSorted) {
        m_AcceccibleCommitts = i_AccessibleCommitSorted;
    }

    public List<Commit> GetHeadBranchAccesibleCommits(Commit activeCommit, Map commitMapPresentation) {
        List<Commit> commitPresentations = new ArrayList<Commit>() {
            @Override
            public boolean contains(Object o) {
                Commit other = (Commit) o;
                boolean contains = false;
                for (Commit commit : this) {
                    if (other.equals(commit)) {
                        contains = true;
                    }
                }
                return contains;
            }
        };
        GetHeadBranchAccesibleCommitsRec(activeCommit, commitMapPresentation, commitPresentations);
        return commitPresentations;
    }

    private void GetHeadBranchAccesibleCommitsRec(Commit activeCommit, Map commitMapPresentation, List<Commit> commitPresentations) {
        Commit commitPresentation = (Commit) commitMapPresentation.get(activeCommit.GetSha1());
        if (!commitPresentations.contains(commitPresentation)) {
            commitPresentations.add(commitPresentation);
            try {
                for (Commit commit : activeCommit.GetParentCommit()) {
                    GetHeadBranchAccesibleCommitsRec(commit, commitMapPresentation, commitPresentations);
                }
            } catch (NullPointerException ex) {
                // do nothing
            }

        }
    }


    public List<Commit> getAccessibleCommitSorted() {
        List<Commit> m_HeadBranchAccesibleCommits = GetHeadBranchAccesibleCommits(m_Magit.GetActiveCommit(), m_Magit.GetCommits());

        m_AcceccibleCommitts = GetHeadBranchAccesibleCommits(m_Magit.GetActiveCommit(), m_Magit.GetCommits());
        m_HeadBranchAccesibleCommits.sort(new Comparator<Commit>() {
            @Override
            public int compare(Commit o1, Commit o2) {
                if (o1.GetDateObject().after(o2.GetDateObject())) {
                    return -1;
                } else if (o1.GetDateObject().before(o2.GetDateObject())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return m_HeadBranchAccesibleCommits;
    }

    private boolean isLocalRepository() {
//        Path path = Paths.get(m_Path.toString() + "/.magit/Branches");
//        File file = new File(path.toString());
//        boolean isLocal = false;
//        File[] subFile = file.listFiles();

        File file = new File(m_Path.toString() + "/.magit/Remote");
        return file.exists();
    }

    public Magit GetMagit() {
        return m_Magit;
    }

    public WorkingCopy GetWC() {
        return m_WC;
    }

    public String GetMainFolderSha_1OfActiveCommit() {

        return m_Magit.GetActiveCommit().GetMainFolderSha_1();
    }

    public void Commit(String i_CommitMessage, UserName i_UserName, Commit i_Parent) {
        Map<String, RepositoryFile> sha1RepositroyFileMap = null;
        if (m_Magit.GetActiveCommit() != null) {
            sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        }
        m_WC.GenerateSystemFiles(i_UserName, m_Magit.GetObjects(), sha1RepositroyFileMap);
        createZipFilesFromCurrentWc(m_WC.GetMainFolder());
        Commit newCommit = createTheCommit(i_CommitMessage, i_UserName, m_WC.GetPath(), i_Parent);
        m_Magit.GetCommits().put(newCommit.GetSha1(), newCommit);
        UpdateActiveBranch(newCommit);
    }

    public boolean IsSomethingToCommit() {
        return !(m_WC.GetMainFolder().GetSha_1().equals(m_Magit.GetActiveCommit().GetMainFolderSha_1()));
//        Map<String, RepositoryFile> sha1RepositroyFileMap = null;
//        if (m_Magit.GetActiveCommit() != null) {
//            sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
//        }
//        m_WC.GenerateSystemFiles(i_UserName, m_Magit.GetObjects(), sha1RepositroyFileMap);
//        File mainFolder = new File(m_WC.GetPath().toString());
//        boolean isThereIsActiveCommit = m_Magit.GetActiveBranch().GetCommit() != null;
//        boolean isSomethingToCommit = false; // noy-validation
//        if (mainFolder.listFiles().length > 1) { // noy-validation
//            if (isThereIsActiveCommit) {
//                String wcSha_1 = m_WC.GetMainFolder().GetSha_1();
//                String activeCommitSha_1 = GetMainFolderSha_1OfActiveCommit();
//                isSomethingToCommit = !wcSha_1.equals(activeCommitSha_1);
//            } else {
//                isSomethingToCommit = true;
//            }
//        }
//        return isSomethingToCommit;
    }

    private void UpdateActiveBranch(Commit i_NewCommit) {
        m_Magit.GetHead().GetActiveBranch().SetCommit(i_NewCommit);
        createTheTextFileCommitInComittedArea(i_NewCommit);
        m_Magit.GetActiveBranch().createBranchTextFile();
    }

    private void createZipFilesFromCurrentWc(RepositoryFile i_RepositoryFile) {
        createZipFilesFromCurrentWcRec(i_RepositoryFile);
    }

    private Commit createTheCommit(String i_CommitMessage, UserName i_UserName, String i_PathToWc, Commit i_Parent) {
        Commit newCommit = createTheObjectCommitInMagitObjects(i_CommitMessage, i_UserName, Paths.get(i_PathToWc), i_Parent);
        return newCommit;

    }

    private void createTheTextFileCommitInComittedArea(Commit i_Commit) {
        String pathToNewCommitInCommitted = m_Path.toString() + "/.magit/" + i_Commit.GetSha1();
        String contentFile = i_Commit.toString();
        CreateTextFile(pathToNewCommitInCommitted, contentFile);
    }

    private Commit createTheObjectCommitInMagitObjects(String i_CommitMessage, UserName i_UserName, Path i_PathToWc, Commit i_Parent) {

        String sha_1MainFolder = m_WC.GetMainFolder().GetSha_1();
        List<Commit> parentsCommit = null;

        if (m_Magit.GetActiveBranch().GetCommit() != null) {
            parentsCommit = new ArrayList<>();
            parentsCommit.add(m_Magit.GetActiveBranch().GetCommit());
            if (i_Parent != null) {
                parentsCommit.add(i_Parent);
            }
        }
        Date commitDate = new Date();
        Commit newCommit = new Commit(sha_1MainFolder, parentsCommit, i_CommitMessage, commitDate, i_UserName, i_PathToWc.toString());
        return newCommit;
    }

    private void createZipFilesFromCurrentWcRec(RepositoryFile i_RepositoryFile) {
        if (i_RepositoryFile != null) {
            String sha_1_RepositoryFile = i_RepositoryFile.GetSha_1();
            if (!m_Magit.GetObjects().containsKey(sha_1_RepositoryFile)) {
                if (i_RepositoryFile instanceof Folder) {
                    Folder folder = ((Folder) i_RepositoryFile);
                    int numOfFilesInFolder = folder.GetRepositoryFiles().size();
                    List<RepositoryFile> innerRepositoryFiles = folder.GetRepositoryFiles();

                    for (int i = 0; i < numOfFilesInFolder; i++) {
                        createZipFilesFromCurrentWcRec(innerRepositoryFiles.get(i));
                    }
                }
                CreateZipFromRepositoryFileInObjectsFolder(i_RepositoryFile);
                AddZipToMap(i_RepositoryFile);
            }
        }
    }

    public void AddZipToMap(RepositoryFile i_RepositoryFile) {
        Path zipPath = Paths.get(m_Magit.GetPath() + "/Objects/" + i_RepositoryFile.GetSha_1() + ".zip");
        m_Magit.GetObjects().put(i_RepositoryFile.GetSha_1(), zipPath.toString());
    }

    private void CreateZipFromRepositoryFileInObjectsFolder(RepositoryFile i_RepositoryFile) {
        CreateTemporaryTxtFileInObjectsFromRepositoryFile(i_RepositoryFile);
        CreateZipFromTemporaryTextFile(i_RepositoryFile);
        new File(m_Magit.GetPath().toString() + "/Objects/" + i_RepositoryFile.GetName()).delete();

    }

    private void CreateZipFromTemporaryTextFile(RepositoryFile i_RepositoryFile) {
        String zipName = i_RepositoryFile.GetSha_1() + ".zip";
        String destination = m_Path.toString() + "/.magit/Objects/" + zipName;
        String source = m_Path.toString() + "/.magit/Objects/" + i_RepositoryFile.GetName();
        Zip(Paths.get(source), Paths.get(destination));
    }

    private void CreateTemporaryTxtFileInObjectsFromRepositoryFile(RepositoryFile i_RepositoryFile) {
        String destenationPath = m_Magit.GetPath().toString() + "/Objects/" + i_RepositoryFile.GetName();
        String contentFile = i_RepositoryFile.toString();
        CreateTextFile(destenationPath, contentFile);
    }

    public String GetBranchDetails() {
        return m_Magit.BranchesDetails();
    }

    public boolean IsExistBranch(String i_BranchName) {
        return m_Magit.IsExistBranch(i_BranchName);
    }

    public LocalBranch CreateNewBranch(String i_UserName, String i_BranchName) {
        return m_Magit.CreateNewBranch(i_UserName, i_BranchName);
    }

    public LocalBranch CreateNewBranchForSpecificCommit(String i_BranchName, Commit i_Commit) {
        String[] branchFullName = i_BranchName.split("/");
        String branchName = branchFullName[1];
        return m_Magit.CreateNewBranchForSpecificCommit(branchName, i_Commit);
    }

    public String ShowCurrentCommit() {
        return m_Magit.ShowCurrrentCommit();
    }

    public boolean IsHeadBranch(String i_BranchName) {
        return m_Magit.IsHeadBranch(i_BranchName);
    }

    public void DeleteBranch(String i_BranchName) {
        m_Magit.DeleteBranch(i_BranchName);
    }

    public void SwitchHeadBranchAndDeployIt(String i_BranchName) {
        m_Magit.SwitchHeadBranch(i_BranchName);
        DeployHeadBranch();
        m_AcceccibleCommitts = getAccessibleCommitSorted();
    }

    public void DeployHeadBranch() {
        if (m_Magit.GetActiveCommit() != null) {
            cleanWC();
            m_Magit.DeployHeadBranch();
            m_WC.GetMainFolder().SetSha_1(m_Magit.GetActiveBranch().GetCommit().GetMainFolderSha_1());
            m_WC.SetMainFolder(m_Magit.GetActiveBranch().GetCommit().GetMainFolder());
        }
    }

    private void cleanWC() {
        m_WC.Clean();
    }

    public List<RepositoryFile> DeployCommitToList() {
        String Sha_1CommitMainFolder = m_Magit.GetActiveCommit().GetMainFolderSha_1();
        List<RepositoryFile> repositoryFiles = new ArrayList<>();
        createRepositoryFileListOfCommitZipRec(Sha_1CommitMainFolder, m_Path, repositoryFiles);
        return repositoryFiles;
    }

    private RepositoryFile createRepositoryFileListOfCommitZipRec(String i_Sha_1Folder, String i_PathToFile, List<RepositoryFile> i_RepositoryFiles) {
        RepositoryFile file = null;
        String pathToZip = m_Path.toString() + "/.magit/Objects/" + i_Sha_1Folder + ".zip";
        String zipContent = ReadFromZip(pathToZip);
        List<RepositoryFile> subFilesInFolder = new ArrayList<>();
        String[] linesOfZip = zipContent.split("\n");
        for (String str : linesOfZip) {
            String[] output = str.split(",");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
            Date date = null;
            try {
                date = dateFormat.parse(output[4]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Path pathToFile = Paths.get(i_PathToFile.toString() + "/" + output[0]);

            if (output[2].equals("Folder")) {
                RepositoryFile subFile = createRepositoryFileListOfCommitZipRec(output[1], pathToFile.toString(), i_RepositoryFiles);
                subFilesInFolder.add(subFile);
                file = new Folder(output[0], output[1], output[3], date, pathToFile.toString(), subFilesInFolder);
                i_RepositoryFiles.add(file);
            } else {
                String content = ReadFromZip(m_Path.toString() + "/.magit/Objects/" + output[1] + ".zip");
                file = new Blob(output[0], output[1], output[3], date, pathToFile.toString(), content);
                i_RepositoryFiles.add(file);
            }
        }
        return file;
    }

    public String GetPath() {
        return m_Path;
    }

    public boolean HasBranchesExceptHead() {
        return false; //m_Magit.HasBranchesExceptHead();
    }

    public boolean HasFilesInWC() {
        return true; // m_WC.HasFilesInWC();
    }

    public RepositoryFile GetCurrentCommitMainFolder() { // Dekel
        return m_Magit.GetCurrentCommitMainFolder();
    }

    public String GetName() {
        return m_Name;
    }

    public Delta GetDelta(UserName i_userName) {
        Map<String, RepositoryFile> sha1RepositroyFileMap = null;
        if (m_Magit.GetActiveCommit() != null) {
            sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        }
        m_WC.GenerateSystemFiles(i_userName, m_Magit.GetObjects(), sha1RepositroyFileMap);
        m_Delta.Clean();
        RepositoryFile currentCommitMainFolder = GetCurrentCommitMainFolder();
        calculateDelta(currentCommitMainFolder, m_WC.GetMainFolder());

        return m_Delta;
    }

    private void calculateDelta(RepositoryFile i_CurrentCommitMainFolder, RepositoryFile i_WCMainFolder) {
        List<RepositoryFile> currentCommitRepositoryFilesList = ((Folder) i_CurrentCommitMainFolder).CopyToList();
        List<RepositoryFile> wcMainFolderList = (((Folder) i_WCMainFolder).CopyToList());

        for (RepositoryFile commitRepositoryFile : currentCommitRepositoryFilesList) {
            boolean existAndSameInWC = false;
            boolean existAndNotSameInWC = false;
            boolean exist = false;
            boolean same = false;
            for (RepositoryFile wcRepositoryFile : wcMainFolderList) {
                if (commitRepositoryFile.GetPath().equals(wcRepositoryFile.GetPath())) {
                    exist = true;
                    same = commitRepositoryFile.GetSha_1().equals(wcRepositoryFile.GetSha_1());
                    if (!same) {
                        m_Delta.GetChanged().add(wcRepositoryFile);
                    }
                }
            }
            if(!exist){
                m_Delta.GetDeleted().add(commitRepositoryFile);
            }
//
//            if (existAndNotSameInWC) {
//                m_Delta.GetChanged().add(wcRepositoryFile);
//            } else if (!existAndSameInWC) {
//                m_Delta.GetDeleted().add(commitRepositoryFile);
//            }
        }

        for (RepositoryFile wcRepositoryFile : wcMainFolderList) {
            boolean existInCommit = false;
            for (RepositoryFile commitRepositoryFile : currentCommitRepositoryFilesList) {
                if (commitRepositoryFile.GetPath().equals(wcRepositoryFile.GetPath())) {
                    existInCommit = true;
                }
            }
            if (!existInCommit) {
                m_Delta.GetAdded().add(wcRepositoryFile);
            }
        }
    }


    public boolean GetIsLocal() {
        return m_IsLocal;
    }

//    public void Fetch() {
//        m_Remote.Fetch();
//    }

    public void Pull() {
        m_Remote.Pull();

        m_Magit.UpdateAfterPull();
        getAccessibleCommitSorted();
        m_Remote.GetRemoteBranches().clear();
        m_Remote.loadRemoteBranches(m_Magit.GetCommits());
    }

    public Remote GetRemote() {
        return m_Remote;
    }

    public void Push() {
        m_Remote.Push();
    }

    public boolean IsSomethingToPush() {
        return m_Remote.IsSomeThingToPush();
    }

    public boolean IsFastMerge() {
        return m_Magit.IsFastMerge();
    }

    public void FastMerge() {
        m_Magit.FastMerge();
    }

    public List<Conflict> GetConflicts() {
        m_WC.Clean();
        return m_Magit.GetConflicts();
    }

    public void SetCommitInMerger() {
        m_Magit.SetCommitInMerger();
    }

    public void InitializeMerger(Branch i_branchToMerge) {
        m_Magit.InitializeMerger(i_branchToMerge);
    }

    public Commit GetActiveCommit() {
        return m_Magit.GetActiveCommit();
    }

    public void PushBranchToRR(LocalBranch i_branch) {
        m_Remote.PushBranchToRR(i_branch);
        m_Remote.GetRemoteBranches().clear();
        m_Remote.loadRemoteBranches(m_Magit.GetCommits());
    }

    public void moveToTheCommonFolder(String i_UserName) {
        String srcPath = m_Path;
        Path pathToCurrentUserNameFolder = Paths.get("C:/magit-ex3/" + i_UserName);
        new File(pathToCurrentUserNameFolder.toString()).mkdir();
        Path newPath = Paths.get(pathToCurrentUserNameFolder.toString() + "/" + m_Name);
        m_Path = newPath.toString();
        try {
            FileUtils.moveDirectory(new File(srcPath), new File(m_Path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DeleteFileBySha_1(String sha_1, String i_UserName) {
        m_WC.DeleteFileBySha_1(sha_1);
        Map sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        m_WC.GenerateSystemFiles(new UserName(i_UserName), m_Magit.GetObjects(), sha1RepositroyFileMap);
    }

    public Commit FindCommitBySha1(String i_Sha1Commit) {
        return m_Magit.FindCommitBySha1(i_Sha1Commit);
    }

    public boolean IsRTB(String i_BranchName) {
        boolean isRTB = false;
        List<RemoteBranch> RBList = m_Remote.GetRemoteBranches();
        for (RemoteBranch remoteBranch : RBList) {
            isRTB = remoteBranch.GetOriginName().equals(i_BranchName);
            if (isRTB)
                break;
        }
        return isRTB;
    }

    public void UpdateAfterPush() {
        m_Magit = new Magit(m_Name, Paths.get(m_Path.toString() + "/.magit"), m_IsLocal);
    }

    public void UpdateAfterPush(String i_BranchName) {
        m_Magit.UpdateAfterPush(i_BranchName);
    }

    public void AddFile(String userName, String path, String content) {
        DekelNoy3rd.Service.Methods.CreateTextFile(path, content);
        Map sha1RepositroyFileMap = ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap();
        m_WC.GenerateSystemFiles(new UserName(userName), m_Magit.GetObjects(), sha1RepositroyFileMap);
    }

    public void ChangeBlob(String userName, String path, String content) {
        m_WC.ChangeBlob(userName, path, content);
        m_WC.GenerateSystemFiles(new UserName(userName), m_Magit.GetObjects(), ((Folder) m_Magit.GetCurrentCommitMainFolder()).CopyToMap());
    }


    public List<PullRequest> GetPullRequests() {
        return m_PullRequest;
    }

    public void AllocatePullRequestsList() {
        m_PullRequest = new ArrayList<>();
    }

    public LocalBranch FindBranchByName(String i_BranchName) {
        return m_Magit.FindBranchByName(i_BranchName);
    }

    public void AcceptPullRequest(int i_PullRequestID) {
        PullRequest pullRequest = FindPullRequest(i_PullRequestID);
        String target = pullRequest.GetTarget();
        String base = pullRequest.GetBase();
        LocalBranch baseBranch = FindBranchByName(base);
        LocalBranch targetBranch = FindBranchByName(target);
        ResetBranch(baseBranch, targetBranch);
        pullRequest.SetClosed();
        m_Magit.GetPushedBranches().remove(target);
    }

    public PullRequest FindPullRequest(int i_PullRequestID) {
        PullRequest pullRequest = null;
        for (PullRequest pullRequestItr : m_PullRequest) {
            if (pullRequestItr.GetID() == i_PullRequestID) {
                pullRequest = pullRequestItr;
                break;
            }
        }
        return pullRequest;
    }

    public void ResetBranch(LocalBranch i_BaseBranch, LocalBranch i_TargetBranch) {
        i_BaseBranch.SetCommit(i_TargetBranch.GetCommit());
        i_BaseBranch.createBranchTextFile();
        SwitchHeadBranchAndDeployIt(i_BaseBranch.m_Name);
    }

    public void RejectPullRequest(int i_PullRequestID) {
        PullRequest pullRequest = FindPullRequest(i_PullRequestID);
        pullRequest.SetReject();
    }

    public void SetDelta(Delta i_Delta) {
        m_Delta = i_Delta;
    }


    public Delta GetDelta(Commit i_commit, Commit i_parentCommit) {
        m_Delta.Clean();
        RepositoryFile CommitMainFolder = i_commit.GetMainFolder();
        RepositoryFile parentCommitCommitMainFolder = i_parentCommit.GetMainFolder();
        calculateDelta(parentCommitCommitMainFolder, CommitMainFolder);
        return m_Delta;
    }


    public Delta GetDelta(String i_Base, String i_Target) {
        LocalBranch baseBranch = FindBranchByName(i_Base);
        LocalBranch targetBranch = FindBranchByName(i_Target);
        return GetDelta(targetBranch.GetCommit(), baseBranch.GetCommit());
    }

    public List<Commit> FindCommittsBetweenTwoBranches(String i_Base, String i_Target) {
        List<Commit> commitList = new ArrayList<>();
        LocalBranch baseBranch = FindBranchByName(i_Base);
        LocalBranch targetBranch = FindBranchByName(i_Target);
        Commit commit = targetBranch.GetCommit();
        while (commit != baseBranch.GetCommit()) {
            commitList.add(commit);
            commit = commit.GetParentCommit().get(0);
        }
        commitList.add(commit);
        return commitList;
    }
}
package logic;

import java.io.*;
import java.nio.file.*;
import java.util.*;;
import static DekelNoy3rd.Service.Methods.*;

public class Magit {
    // private List<IRemoteBranch> m_RemoteBranches;
    private List<LocalBranch> m_Branches;
    private String m_Path;
    private Map<String,String> m_Objects; // the wc is working. this is the problem
    private Head m_Head;
    private Map<String, Commit> m_Commits;
    private String m_RepositoryName;
    private List<String> m_PushedBranches;
    //   private BranchLoader m_BranchLoader;
    //private final boolean m_IsLocal;




    public Magit(String i_RepositoryName, Path i_Path, boolean i_IsLocal) {
        m_Branches = new ArrayList<>();
        m_Path = i_Path.toString();
        m_Objects = new HashMap<>();
        restoreObjectsMap();
        m_Commits = new HashMap<>();
        loadCommits();
        m_Head = new Head(Paths.get(i_Path + "/Branches/HEAD").toString(), m_Commits);
        m_Branches.add(m_Head.GetActiveBranch());
        CreateTextFile(i_Path.toString() + "/Name", i_RepositoryName);
        m_RepositoryName = i_RepositoryName;
        loadNonActiveBranches();
        m_PushedBranches = new ArrayList<>();
    }

    private void loadCommits() {
        CommitLoader commitLoader = new CommitLoader(Paths.get(m_Path));
        File []commitFiles = Paths.get(m_Path).toFile().listFiles();

        for (File commitFile: commitFiles){
            if (commitFile.isFile()){
                if (!commitFile.getName().equals("Name") && !commitFile.getName().equals("Remote")){
                    Commit commit = commitLoader.GetCommit(commitFile, m_Commits);
                    insertCommitAndAllHisAncestorToMap(commit);
                }
            }

        }
    }

    private void insertCommitAndAllHisAncestorToMap(Commit i_commit) {
        insertCommitToMapRec(i_commit);
    }

    private void insertCommitToMapRec(Commit i_commit) {
        if (!m_Commits.containsKey(i_commit.GetSha1())){
            m_Commits.put(i_commit.GetSha1(),i_commit);

            if (i_commit.GetParentCommit() != null){
                insertCommitToMapRec(i_commit.GetParentCommit().get(0));
                if (i_commit.GetParentCommit().size() == 2){
                    insertCommitToMapRec(i_commit.GetParentCommit().get(1));
                }
            }
        }
    }

    private void restoreObjectsMap() {
        File file = new File (m_Path.toString() + "/Objects");
        File[] subFiles = file.listFiles();
        for (File subFile: subFiles){
            String sha1 = new StringBuilder(subFile.getName()).substring(0, subFile.getName().length() - 4);
            m_Objects.put(sha1, subFile.toPath().toString());
        }
    }


    public List<LocalBranch> GetBranches() {return m_Branches;}

    public String GetPath() {
        return m_Path;
    }

    public Map<String, Commit> GetCommits() {
        return m_Commits;
    }

    public LocalBranch GetActiveBranch() {return m_Head.GetActiveBranch();}

    public Map<String, String> GetObjects() {return m_Objects;}

    public Head GetHead() {
        return m_Head;
    }

    private void loadNonActiveBranches() {
        Path pathToBranchesFolder = Paths.get(m_Path + "/Branches");
        File[] branchesTextFiles = new File(pathToBranchesFolder.toString()).listFiles();
        for (int i = 0; i < branchesTextFiles.length; i++) {
            Path branchPath = Paths.get(branchesTextFiles[i].getAbsolutePath());
            boolean isActiveBranch = branchPath.equals(Paths.get(m_Head.GetActiveBranch().GetPath()).normalize().toAbsolutePath());
            boolean isHeadFile = (branchPath.getName(branchPath.getNameCount() - 1)).toString().equals("HEAD");
            if (!isActiveBranch && !isHeadFile && branchesTextFiles[i].isFile()) {
                String SHA1ofCommit = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(branchPath.toString());
                Commit commit = m_Commits.get(SHA1ofCommit);
                m_Branches.add(new LocalBranch(branchPath.toFile().getName(), commit, branchPath.toString()));
            }
        }
    }

    public String BranchesDetails(){
        String branchDetails = "";
        if (m_Head.GetActiveBranch().GetCommit() == null)
        {
            branchDetails = "There are no branches which point to any commit yet!";
        }
        else {
            branchDetails = "All branches details:" + System.lineSeparator();
            branchDetails += "=====================" + System.lineSeparator();
            for (Branch branch : m_Branches) {
                branchDetails += branch.toString();
                if (branch == m_Head.GetActiveBranch()) {
                    branchDetails += "This is the head branch" + System.lineSeparator();
                }
                branchDetails += "================================================" + System.lineSeparator();
            }
        }
        return branchDetails;
    }

    public Commit GetActiveCommit(){
        return m_Head.GetActiveCommit();
    }

    public boolean IsExistBranch (String i_BranchName){
        boolean isExistBranch = false;
        for (LocalBranch localBranch : m_Branches) {
            if (localBranch.GetName().equals(i_BranchName)){
                isExistBranch = true;
            }
        }
        return isExistBranch;
    }

    public LocalBranch CreateNewBranch(String i_UserName, String i_BranchName){
        Commit commit = m_Head.GetActiveBranch().GetCommit();
        //Path newBranchPath = Paths.get(m_Path.toString() + "/Branches/" + i_BranchName);
        Path newBranchPath = Paths.get("c:/magit-ex3/" + i_UserName +"/"+m_RepositoryName+"/.magit/Branches/"+i_BranchName);
        //Path newBranchPath = Paths.get(m_Path +"/Branches/" + i_BranchName);
        LocalBranch newBranch = new LocalBranch(i_BranchName,commit,newBranchPath.toString());
        m_Branches.add(newBranch);
        return newBranch;
    }


    public LocalBranch CreateNewBranchForSpecificCommit(String i_BranchName, Commit i_Commit) {
        Path newBranchPath = Paths.get(m_Path.toString() + "/Branches/" + i_BranchName);
        LocalBranch newBranch = new LocalBranch(i_BranchName, i_Commit, newBranchPath.toString());
        m_Branches.add(newBranch);
        return newBranch;
    }

    public boolean IsHeadBranch(String i_BranchName) {
        return m_Head.IsHeadBranch(i_BranchName);
    }

    public String ShowCurrrentCommit() {
        return m_Head.ShowCommit();
    }


    public void DeleteBranch(String branchName) {
        LocalBranch branchToRemove = null;

        for (LocalBranch localBranch: m_Branches)
        {
            if (localBranch.GetName().equals(branchName)){
                branchToRemove = localBranch;
            }
        }
        branchToRemove.DeleteBranchTextFile();
        m_Branches.remove(branchToRemove);

    }

//    public String BranchCommitHistory() {
//        String branchCommitsHistory = "";
//        Commit currentCommit = m_Head.GetActiveBranch().GetCommit();
//        while(currentCommit != null){
//            branchCommitsHistory += currentCommit.ShowCommitDetails();
//            currentCommit = currentCommit.GetParentCommit();
//        }
//        if(branchCommitsHistory.equals("")){
//            branchCommitsHistory = "This branch does not point for any committ";
//        }
//        return branchCommitsHistory;
//    }

    public boolean HasBranchesExceptHead() {
        return m_Branches.size() > 1;
    }


    public void DeployHeadBranch() {
        m_Head.DeployHeadBranch();
    }

    public void SwitchHeadBranch(String i_BranchName) {
        LocalBranch newActiveBranch = null;
        for (LocalBranch localBranch : m_Branches){
            if(localBranch.GetName().equals(i_BranchName)){
                newActiveBranch = localBranch;
            }
        }
        m_Head.SetActiveBranch(newActiveBranch);
        //m_Head.SetActiveBranch(newActiveBranch);
    }

    public RepositoryFile GetCurrentCommitMainFolder() { // Dekel
        return m_Head.GetCurrentCommitMainFolder();
    }

    public boolean IsFastMerge() {
        return m_Head.IsFastMerge();
    }

    public void FastMerge() {
        m_Head.FastMerge();
    }

    public void SetCommitInMerger() {
        m_Head.SetCommitInMerger(m_Commits);
    }

    public void InitializeMerger(Branch i_branchToMerge) {
        m_Head.InitializeMerger(i_branchToMerge);
    }

    public List<Conflict> GetConflicts() {
        return m_Head.GetConflicts();
    }

    public Commit GetLastCommit() {
        Date lastDate = ((Commit) m_Commits.values().toArray()[0]).GetDateObject();
        Commit lastCommit =(Commit) m_Commits.values().toArray()[0];
        for (Commit commit: m_Commits.values()){
            if (commit.GetDateObject().after(lastDate)){
                lastCommit = commit;
            }
        }
        return lastCommit;
    }

    public Commit FindCommitBySha1(String i_sha1Commit) {
        return m_Commits.get(i_sha1Commit);
    }

    public void UpdateAfterPush() {
        m_Branches.clear();
        m_Commits.clear();
        m_Objects.clear();
        restoreObjectsMap();
        loadCommits();
        m_Head = new Head(Paths.get(m_Path + "/Branches/HEAD").toString(), m_Commits);
        m_Branches.add(m_Head.GetActiveBranch());
        loadNonActiveBranches();
    }

    public void UpdateAfterPush(String i_BranchName) {
        m_Branches.clear();
        m_Commits.clear();
        m_Objects.clear();
        restoreObjectsMap();
        loadCommits();
        m_Head = new Head(Paths.get(m_Path + "/Branches/HEAD").toString(), m_Commits);
        m_Branches.add(m_Head.GetActiveBranch());
        loadNonActiveBranches();
        m_PushedBranches.add(i_BranchName);
    }

    public void UpdateAfterPull() {
        m_Objects.clear();
        restoreObjectsMap();
        m_Commits.clear();
        loadCommits();
        UpdateAfterPush();
    }

    public LocalBranch FindBranchByName(String i_BranchName) {
        LocalBranch branch = null;
        for(LocalBranch localBranch : m_Branches){
            if(localBranch.GetName().equals(i_BranchName))
                branch = localBranch;
        }
        return branch;
    }

    public List<String> GetPushedBranches() {
        return m_PushedBranches;
    }
}
package logic;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static DekelNoy3rd.Service.Methods.*;

public class Head {
    private String m_Path;
    private LocalBranch m_ActiveBranch;
    private Merger m_Merger;

    public Head(String i_Path, Map<String,Commit> i_sha_1CommitMap) {
        m_Path = i_Path;
        CreateHeadTxtFile();
        m_ActiveBranch = restoreActiveBranch(i_sha_1CommitMap);
        m_Merger = new Merger(m_ActiveBranch);
    }

    public void SetMerger(Merger i_Merger) {
        m_Merger = i_Merger;
    }

    public Merger GetMerger() {
        return m_Merger;
    }

    private void CreateHeadTxtFile() {
        File file = new File(m_Path.toString());
        if (!file.exists()){
            CreateTextFile(m_Path.toString(),"master");
            CreateTextFile(Paths.get(m_Path).getParent().toString() + "/master","");
        }
    }

    public LocalBranch GetActiveBranch() {
        return m_ActiveBranch;
    }

    public void SetActiveBranch(LocalBranch i_ActiveBranch) {
        m_ActiveBranch = i_ActiveBranch;
        DekelNoy3rd.Service.Methods.CreateTextFile(m_Path.toString(), i_ActiveBranch.GetName());
        m_Merger = new Merger(i_ActiveBranch);
    }

    private LocalBranch restoreActiveBranch(Map<String,Commit> i_sha_1CommitMap) {
        LocalBranch activeBranch = null;
        try {
            // here the head isnt deleted
            Scanner scanner = new Scanner(new File(m_Path.toString()));
            String activeBranchName = scanner.nextLine();
            scanner.close();

            Path pathToActiveBranch = Paths.get(Paths.get(m_Path).getParent().toString() + "/" + activeBranchName).normalize();
            String SHA1ofCurrentCommit = DekelNoy3rd.Service.Methods.ReadContentOfTextFile(pathToActiveBranch.toString());
            Commit currentCommit = i_sha_1CommitMap.get(SHA1ofCurrentCommit);
            activeBranch = new LocalBranch(activeBranchName, currentCommit, pathToActiveBranch.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return activeBranch;
    }

    public String ShowCommit() {
        return m_ActiveBranch.ShowCommit();
    }

    public boolean IsHeadBranch(String i_BranchName) {
        return m_ActiveBranch.GetName().equals(i_BranchName);
    }

    public void DeployHeadBranch() {
        m_ActiveBranch.Deploy();
    }

    public Commit GetActiveCommit() {
        return m_ActiveBranch.GetCommit();
    }

    public RepositoryFile GetCurrentCommitMainFolder() { //Dekel
        return m_ActiveBranch.GetCommitMainFolder();
    }

    public boolean IsFastMerge() {
        return m_Merger.IsFastMerge();
    }

    public void FastMerge() {
        m_Merger.FastMerge();
    }

    public void SetCommitInMerger(Map<String, Commit> i_Commits) {
        m_Merger.SetCommits(i_Commits);
    }

    public void InitializeMerger(Branch i_branchToMerge) {
        m_Merger.InitializeMerger(i_branchToMerge);
    }

    public List<Conflict> GetConflicts() {
        return m_Merger.GetConflicts();
    }
}
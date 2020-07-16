package logic;

import java.nio.file.Path;
import java.util.SplittableRandom;

public abstract class Branch {
    protected String m_Name;
    protected Commit m_Commit;
    protected String m_Path;

    public String GetPath() {
        return m_Path;
    };

    public Branch(String i_Name, Commit i_Commit, String i_Path) {
        m_Name = i_Name;
        m_Commit = i_Commit;
        m_Path = i_Path;
    }

    public String GetName(){
        return m_Name;
    };

    public Commit GetCommit(){
        return m_Commit;
    };
}
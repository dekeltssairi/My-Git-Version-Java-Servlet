package logic;

import java.util.Date;

public class PullRequest {
    private final Date m_Date;
    private final int m_ID;
    private String m_Status;
    private String m_Target;
    private String m_Base;
    private String m_Message;
    private UserName m_Requester;

    public PullRequest(String i_TargetName, String i_BaseName, String i_PrMessage, UserName i_Requester, int i_ID) {
        m_Target = i_TargetName;
        m_Base = i_BaseName;
        m_Message = i_PrMessage;
        m_Requester = i_Requester;
        m_Status = "Open";
        m_Date = new Date();
        m_ID = i_ID;
    }

    public void SetClosed(){
        m_Status = "Close";
    }

    public void SetReject(){
        m_Status = "Reject";
    }

    public int GetID() {
        return m_ID;
    }

    public String GetTarget() {
        return  m_Target;
    }

    public String GetBase() {
        return m_Base;
    }

    public UserName GetRequester() {
        return m_Requester;
    }
}

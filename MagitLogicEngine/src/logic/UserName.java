package logic;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserName {
    // list of message
    private final String m_Name;
    private List<String> m_Messages;

    public UserName(String i_Name) {
        m_Name = i_Name;
        m_Messages = new ArrayList<>();
    }
    public UserName() {
        m_Name = "Administrator"; // Change to "Define" or const or what ever
        m_Messages = new ArrayList<>();
    }

    public String GetName() {
        return m_Name;
    }

    @Override
    public String toString() {
        return m_Name;
    }

    @Override
    public boolean equals(Object obj) {
        UserName userName = (UserName) obj;
        return userName.GetName().equals(this.m_Name);
    }

    public void AddMessage(String i_Message){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        //Date date = new Date();
        String todayAsString = df.format(today);

        m_Messages.add(todayAsString + ": " +i_Message);
    }

    public void ClearMessages() {
        m_Messages.clear();
    }

    public List<String> GetMessages() {
        return m_Messages;
    }
}

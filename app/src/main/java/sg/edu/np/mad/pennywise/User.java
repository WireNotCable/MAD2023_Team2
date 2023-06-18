package sg.edu.np.mad.pennywise;

public class User {
    private String username;
    private String password;
    private String NRIC;
    private String ContactNo;

    public User(){

    }
    public User(String username, String password,String NRIC,String ContactNo) {
        this.username = username;
        this.password = password;
        this.NRIC = NRIC;
        this.ContactNo = ContactNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNRIC() {
        return NRIC;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }
}

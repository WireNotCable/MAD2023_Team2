package sg.edu.np.mad.pennywise;

public class FriendClass {
    private String UID;
    private String name;
    private String email;
    private String contact;

    public FriendClass(String UID, String name, String email, String contact) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

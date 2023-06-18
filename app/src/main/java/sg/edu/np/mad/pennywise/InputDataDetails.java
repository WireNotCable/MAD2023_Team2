package sg.edu.np.mad.pennywise;

public class InputDataDetails {
    private String UID;
    private String NRIC;
    private String Contact;

    public InputDataDetails(String UID, String NRIC, String contact) {
        this.UID = UID;
        this.NRIC = NRIC;
        Contact = contact;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNRIC() {
        return NRIC;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }
}

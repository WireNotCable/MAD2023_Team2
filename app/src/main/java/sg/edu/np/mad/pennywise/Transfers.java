package sg.edu.np.mad.pennywise;

public class Transfers {
    public String amount;
    public String comments;
    public String fromUID;
    public String toUID;
    public String transferDate;
    public String type;

    public Transfers(String amount, String comments, String fromUID, String toUID, String transferDate, String type) {
        this.amount = amount;
        this.comments = comments;
        this.fromUID = fromUID;
        this.toUID = toUID;
        this.transferDate = transferDate;
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFromUID() {
        return fromUID;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

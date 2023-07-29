package sg.edu.np.mad.pennywise2.models;

// Model for Transaction object //
public class Transaction {
    private String transId;
    private String transTitle;
    private String transDate;
    private String toUID;
    private String fromUID;
    private double transAmt;
    private String transType;


    public String getTransId() {
        return transId;
    }


    public void setTransId(String transTitle) {
        this.transTitle = transId;
    }
    public String getTransTitle() {
        return transTitle;
    }


    public void setTransTitle(String transTitle) {
        this.transTitle = transTitle;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public double getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(double transAmt) {
        this.transAmt = transAmt;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }



    public Transaction(String transId, String transTitle, String transDate,  double transAmt, String transType) {
        this.transId = transId;
        this.transTitle = transTitle;
        this.transDate = transDate;
        this.transAmt = transAmt;
        this.transType = transType;
    }
}

package sg.edu.np.mad.pennywise;

import android.widget.DatePicker;

import com.google.type.Date;
import com.google.type.DateTime;

public class Transaction {

    private String transTitle;
    private String transDate;
    private double transAmt;
    private String transType;

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
    public Transaction(String transTitle, String transDate, double transAmt, String transType) {
        this.transTitle = transTitle;
        this.transDate = transDate;
        this.transAmt = transAmt;
        this.transType = transType;
    }


}

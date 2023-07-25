package sg.edu.np.mad.pennywise2;

public class IndivisualGoalI {
    private String name;
    private double amount;
    private String date;
    private String uid;

    public IndivisualGoalI(String name, double amount, String date, String uid) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

package sg.edu.np.mad.pennywise;

public class Goal {
    private String uid;
    private String name;
    private double amount;
    private double current;



    public Goal(String uid, String name, double amount, double current) {
        this.uid = uid;
        this.name = name;
        this.amount = amount;
        this.current = current;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }
}

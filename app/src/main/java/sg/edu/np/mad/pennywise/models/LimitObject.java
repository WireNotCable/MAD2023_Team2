package sg.edu.np.mad.pennywise.models;

public class LimitObject {

    private String startdate;

    private String enddate;

    private double spendlimit;
    private double fallsbelow;

    public LimitObject() {
    }

    public LimitObject(String startdate, String enddate, double spendlimit, double fallsbelow) {
        this.startdate = startdate;
        this.enddate = enddate;
        this.spendlimit = spendlimit;
        this.fallsbelow = fallsbelow;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public double getSpendlimit() {
        return spendlimit;
    }

    public void setSpendlimit(double spendlimit) {
        this.spendlimit = spendlimit;
    }

    public double getFallsbelow() {
        return fallsbelow;
    }

    public void setFallsbelow(double fallsbelow) {
        this.fallsbelow = fallsbelow;
    }
}

package sg.edu.np.mad.pennywise2.models;

public class Card {
    private String numCard;
    private String xpDate;
    private String threeDigitNum;
    private String cardNaming;
    private String houseAddr;
    private double balance;

    public String getNumCard() {
        return numCard;
    }


    public void setNumCard(String numCard) {
        this.numCard = numCard;
    }

    public String getXpDate() {
        return xpDate;
    }

    public void setXpDate(String xpDate) {
        this.xpDate = xpDate;
    }

    public String getThreeDigitNum() {
        return threeDigitNum;
    }

    public void setThreeDigitNum(String threeDigitNum) {
        this.threeDigitNum = threeDigitNum;
    }

    public String getCardNaming() {
        return cardNaming;
    }

    public void setCardNaming(String cardNaming) {
        this.cardNaming = cardNaming;
    }
    public String getHouseAddr() {
        return houseAddr;
    }

    public void setHouseAddr(String houseAddr) {
        this.houseAddr = houseAddr;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Card(String cardNaming, String threeDigitNum, String xpDate, String houseAddr, String numCard, double balance) {

        this.threeDigitNum = threeDigitNum;
        this.cardNaming = cardNaming;
        this.numCard= numCard;
        this.xpDate = xpDate;
        this.houseAddr = houseAddr;
        this.balance = balance;
    }
}

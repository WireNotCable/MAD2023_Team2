package sg.edu.np.mad.pennywise.models;

import androidx.annotation.Keep;
import java.io.Serializable;

import com.google.firebase.database.Exclude;

public class CryptoModel implements Serializable  {
    @Exclude
    private String name;
    @Exclude private String symbol;
    @Exclude private double price;
    private double percentChange1h;
    private double percentChange24h;
    private double percentChange7d;
    private double percentChange30d;
    private double percentChange60d;
    private double percentChange90d;


    @Keep
    public CryptoModel(String name, String symbol, double price, double percentChange90d, double percentChange60d, double percentChange30d, double percentChange7d,double percentChange24h, double percentChange1h) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.percentChange90d = percentChange90d;
        this.percentChange1h = percentChange1h;
        this.percentChange24h = percentChange24h;
        this.percentChange7d = percentChange7d;
        this.percentChange60d = percentChange60d;
        this.percentChange30d = percentChange30d;
    }

    @Keep
    public String getName() {
        return name;
    }

    @Keep
    public void setName(String name) {
        this.name = name;
    }

    @Keep
    public String getSymbol() {
        return symbol;
    }

    @Keep
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Keep
    public double getPrice() {
        return price;
    }

    @Keep
    public void setPrice(double price) {
        this.price = price;
    }
    public double getPercentChange1h() {
        return percentChange1h;
    }

    public double getPercentChange24h() {
        return percentChange24h;
    }

    public double getPercentChange7d() {
        return percentChange7d;
    }

    public double getPercentChange30d() {
        return percentChange30d;
    }

    public double getPercentChange60d() {
        return percentChange60d;
    }

    public double getPercentChange90d() {
        return percentChange90d;
    }

}

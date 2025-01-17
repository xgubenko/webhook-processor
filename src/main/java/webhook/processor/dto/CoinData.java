package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class CoinData extends PositionState implements Serializable {
    private String hullsuite;
    private String macd;

    public CoinData() {
    }

    public CoinData(String code, Double quantity, Double price) {
        super(code, quantity, price);
    }

    public CoinData(String hullsuite, String macd) {
        this.hullsuite = hullsuite;
        this.macd = macd;
    }

    public CoinData(String code, Double quantity, Double price, String hullsuite, String macd) {
        super(code, quantity, price);
        this.hullsuite = hullsuite;
        this.macd = macd;
    }

    public String getHullsuite() {
        return hullsuite;
    }

    public void setHullsuite(String hullsuite) {
        this.hullsuite = hullsuite;
    }

    public String getMacd() {
        return macd;
    }

    public void setMacd(String macd) {
        this.macd = macd;
    }

    @Override
    public String toString() {
        return "CoinData{" +
                "ticker='" + code + '\'' +
                ", hullsuite='" + hullsuite + '\'' +
                ", macd='" + macd + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}



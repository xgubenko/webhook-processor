package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinData {
    private String code;
    private String hullsuite;
    private String macd;
    private Double quantity;
    private Double price;


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



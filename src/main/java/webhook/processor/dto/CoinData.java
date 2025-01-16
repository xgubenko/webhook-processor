package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinData implements Serializable {
    private String code;
    private String hullsuite;
    private String macd;
    private Double quantity = 0.0;
    private Double price = 0.0;


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



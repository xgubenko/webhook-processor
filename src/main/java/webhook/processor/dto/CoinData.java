package webhook.processor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;


@Getter
@Setter
@NoArgsConstructor
public class CoinData extends PositionState {
    @Serial
    private static final long serialVersionUID = 42L;
    private String hullsuite;
    private String macd;
    private String macdl;

    public CoinData(String code, Long quantity, Double price, String hullsuite, String macd, String macdl) {
        super(code, quantity, price);
        this.hullsuite = hullsuite;
        this.macd = macd;
        this.macdl = macdl;
    }

    public CoinData(String hullsuite, String macd, String macdl) {
        this.hullsuite = hullsuite;
        this.macd = macd;
        this.macdl = macdl;
    }

    @Override
    public String toString() {
        return "CoinData{" +
                "code='" + code + '\'' +
                ", hullsuite='" + hullsuite + '\'' +
                ", macd='" + macd + '\'' +
                ", macdl='" + macd + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}



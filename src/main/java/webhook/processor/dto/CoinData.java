package webhook.processor.dto;

import lombok.*;

import java.io.Serial;


@Getter
@Setter
@NoArgsConstructor
public class CoinData extends PositionState {
    @Serial
    private static final long serialVersionUID = 42L;
    private String hullsuite;
    private String macd;

    public CoinData(String code, String hullsuite, String macd) {
        super(code);
        this.hullsuite = hullsuite;
        this.macd = macd;
    }

    public CoinData(String hullsuite, String macd) {
        this.hullsuite = hullsuite;
        this.macd = macd;
    }

    @Override
    public String toString() {
        return "CoinData{" +
                "code='" + code + '\'' +
                ", hullsuite='" + hullsuite + '\'' +
                ", macd='" + macd + '\'' +
                '}';
    }
}



package webhook.processor.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTradingViewRequest {
    private String clientId;
    private String api;
    private String code;
    private Double quantity;
    private BinanceTransactionDirection direction;
    private String secret;

    @Override
    public String toString() {
        return "BinanceTradingViewRequest{" +
                "clientId='" + clientId + '\'' +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                ", direction=" + direction +
                '}';
    }
}

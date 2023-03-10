package webhook.processor.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradingViewRequest {
    private String clientId;
    private String api;
    private String code;
    private Integer quantity;
    private FinamTransactionDirection direction;
    private String secret;

    @Override
    public String toString() {
        return "TradingViewRequest{" +
                "clientId='" + clientId + '\'' +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                ", direction=" + direction +
                '}';
    }
}

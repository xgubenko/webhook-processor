package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradingViewRequest {
    private String clientId;
    private String api;
    private String code;
    private Integer quantity;
    private TransactionDirection direction;
}

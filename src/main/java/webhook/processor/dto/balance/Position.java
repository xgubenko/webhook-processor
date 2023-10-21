package webhook.processor.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Position {
    private String securityCode;
    private Double currentPrice;
    private Double averagePrice;
    private Long balance;
    private Double unrealizedProfit;
}

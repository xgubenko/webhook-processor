package webhook.processor.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTradingViewRequest {

    private String code;
    private Double quantity;
    private String indicator;
    private String indicatorDirection;
    private Double price;

    @Override
    public String toString() {
        return "BinanceTradingViewRequest{" +
                "code='" + code + '\'' +
                ", quantity=" + quantity +
                ", indicator='" + indicator + '\'' +
                ", indicatorDirection='" + indicatorDirection + '\'' +
                ", price=" + price +
                '}';
    }
}

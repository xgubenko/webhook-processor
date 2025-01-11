package webhook.processor.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTradingViewRequest {
    private String code;
    private Double quantity;
    private String direction;
    private Double price;

    @Override
    public String toString() {
        return "BinanceTradingViewRequest{" +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                ", direction=" + direction +
                ", price=" + price +
                '}';
    }
}

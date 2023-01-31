package webhook.processor.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderCondition {
    private OrderConditionType type;
    private Double price;
}

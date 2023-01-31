package webhook.processor.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderValidBefore {

    //Time validation for order Установка временных рамок действия заявки
    private OrderValidBeforeType type;
}

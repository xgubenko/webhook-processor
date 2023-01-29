package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderValidBefore {

    //Time validation for order Установка временных рамок действия заявки
    private OrderValidBeforeType type;

    //Time of order registration on the server in UTC Время, когда заявка была зарегистрирована на сервере. В UTC
    private LocalDateTime time;
}

package webhook.processor.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BalanceResponse {
    BalanceData data;
}

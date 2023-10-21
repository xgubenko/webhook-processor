package webhook.processor.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BalanceData {
    private Double equity;
    private List<Position> positions;
}

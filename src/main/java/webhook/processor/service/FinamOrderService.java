package webhook.processor.service;

import org.springframework.http.ResponseEntity;
import webhook.processor.dto.TradingViewRequest;

public interface FinamOrderService {
    public void process(TradingViewRequest request);

    public String checkToken(TradingViewRequest request);
}

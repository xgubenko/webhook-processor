package webhook.processor.controller;

import org.springframework.http.ResponseEntity;
import webhook.processor.dto.TradingViewRequest;

public interface WebhookController {
    ResponseEntity<Object> createNewOrder(TradingViewRequest request);

    ResponseEntity<Object> checkToken(TradingViewRequest request);
}

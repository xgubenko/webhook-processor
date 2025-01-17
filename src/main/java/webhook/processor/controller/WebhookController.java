package webhook.processor.controller;

import org.springframework.http.ResponseEntity;
import webhook.processor.dto.CoinData;
import webhook.processor.dto.PositionState;

import java.util.Map;


public interface WebhookController<T extends PositionState> {
    ResponseEntity<Object> createNewOrder(String request);

    ResponseEntity<Map<String, CoinData>> getPositionsState();
    ResponseEntity<Object> checkToken(String request);
}

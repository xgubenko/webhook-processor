package webhook.processor.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webhook.processor.controller.WebhookController;
import webhook.processor.dto.TradingViewRequest;
import webhook.processor.service.FinamOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhook")
public class WebhookControllerImpl implements WebhookController {

    private final FinamOrderService orderService;

    @Override
    @PostMapping
    public ResponseEntity<Object> createNewOrder(@RequestBody TradingViewRequest request) {
        orderService.process(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PostMapping("/token")
    public ResponseEntity<Object> checkToken(@RequestBody TradingViewRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.checkToken(request));
    }
}

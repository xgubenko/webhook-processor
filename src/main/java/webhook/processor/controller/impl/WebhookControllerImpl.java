package webhook.processor.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webhook.processor.dto.TradingViewRequest;
import webhook.processor.service.FinamOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhook")
public class WebhookControllerImpl {

    private final FinamOrderService orderService;

    @PostMapping
    public ResponseEntity<Object> createNewOrder(@RequestBody TradingViewRequest request) {
        orderService.process(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

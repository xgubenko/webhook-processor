package webhook.processor.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webhook.processor.controller.WebhookController;
import webhook.processor.dto.TradingViewRequest;
import webhook.processor.dto.TransactionDirection;
import webhook.processor.service.FinamOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webhook")
public class WebhookControllerImpl implements WebhookController {

    private final FinamOrderService orderService;

    @Override
    @PostMapping(consumes="text/plain")
    public ResponseEntity<Object> createNewOrder(@RequestBody String request) {
        orderService.process(initRequest(request));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    @PostMapping(value="/token", consumes="text/plain")
    public ResponseEntity<Object> checkToken(@RequestBody String request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.checkToken(initRequest(request)));
    }

    private TradingViewRequest initRequest(String s) {
        TradingViewRequest  request = new TradingViewRequest();

        //clientId api code quantity direction
        String[] arr = s.split(" ");

        request.setClientId(arr[0]);
        request.setApi(arr[1]);
        request.setCode(arr[2]);
        request.setQuantity(Integer.parseInt(arr[3]));

        if(arr[4].equals("buy")) request.setDirection(TransactionDirection.Buy);
        else request.setDirection(TransactionDirection.Sell);

        return request;
    }
}

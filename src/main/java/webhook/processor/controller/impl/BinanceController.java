package webhook.processor.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.BinanceTransactionDirection;
import webhook.processor.dto.TradingViewRequest;
import webhook.processor.dto.FinamTransactionDirection;
import webhook.processor.service.BinanceOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/binance")
public class BinanceController {

    private final BinanceOrderService binanceOrderService;

    @PostMapping(consumes="text/plain")
    public ResponseEntity<Object> createNewOrder(@RequestBody String request) {
        binanceOrderService.process(initRequest(request));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private BinanceTradingViewRequest initRequest(String s) {
        BinanceTradingViewRequest request = new BinanceTradingViewRequest();

        //clientId api code quantity {{strategy.order.action}} secret
        String[] arr = s.split(" ");

        request.setClientId(arr[0]);
        request.setApi(arr[1]);
        request.setCode(arr[2]);
        request.setQuantity(Double.parseDouble(arr[3]));

        if(arr[4].equals("buy")) request.setDirection(BinanceTransactionDirection.BUY);
        else request.setDirection(BinanceTransactionDirection.SELL);

        request.setSecret(arr[5]);

        return request;
    }
}

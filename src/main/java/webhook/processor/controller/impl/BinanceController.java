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

        //code quantity direction {{strategy.order.action}}
        String[] arr = s.split(" ");

        String direction = "";
        if(arr[2].equals("buy")){
            direction = "BUY";
        }
        else {
            direction = "SELL";
        }

        request.setCode(arr[0]);
        request.setQuantity(Double.parseDouble(arr[1]));
        request.setDirection(direction);
        request.setPrice(Double.parseDouble(arr[3]));

        return request;
    }
}

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
//        PENGUUSDT 250 macd down {{price}}
        BinanceTradingViewRequest request = new BinanceTradingViewRequest();

        //code quantity indicator side {{strategy.order.action}}
        String[] arr = s.split(" ");
        var code = arr[0];
        var quantity = Double.parseDouble(arr[1]);
        var indicator = arr[2];
        var indicatorDirection = arr[3];
        var price = Double.parseDouble(arr[4]);

        request.setCode(code);
        request.setQuantity(quantity);
        request.setIndicator(indicator);
        request.setIndicatorDirection(indicatorDirection);
        request.setPrice(price);

        return request;
    }
}

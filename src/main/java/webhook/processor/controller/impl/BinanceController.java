package webhook.processor.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;
import webhook.processor.service.BinanceOrderService;

import java.util.Map;

/**
 * Integration TradingView webhooks and Binance Futures API.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/binance")
public class BinanceController {

    private final BinanceOrderService binanceOrderService;

    /**
     * Get information about current state of indicators for each coin.
     *
     * @return Map of ticker as a key and its local data as a value.
     */
    @GetMapping("/coins")
    public ResponseEntity<Map<String, CoinData>> getCoinData() {
        var response = binanceOrderService.getCoinData();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Process request from TradingView in the following order: code, quantity, indicator, direction, price.
     * example: PENGUUSDT 250 macd down 0.355
     *
     * @param request - webhook request from TradingView.
     * @return code 200 if no problems occurred.
     */
    @PostMapping(consumes = "text/plain")
    public ResponseEntity<Object> createNewOrder(@RequestBody String request) {
        binanceOrderService.process(initRequest(request));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Parse TradingView webhook string into something usable.
     * @param s - webhook string
     * @return pojo to work with.
     */
    private BinanceTradingViewRequest initRequest(String s) {
        var arr = s.split(" ");
        return BinanceTradingViewRequest
                .builder()
                .code(arr[0])
                .quantity(Double.parseDouble(arr[1]))
                .indicator(arr[2])
                .indicatorDirection(arr[3])
                .price(Double.parseDouble(arr[4]))
                .build();
    }
}

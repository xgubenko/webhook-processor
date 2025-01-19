package webhook.processor.controller.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webhook.processor.controller.WebhookController;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;
import webhook.processor.service.BinanceOrderService;

import java.util.Map;

/**
 * Integration between TradingView webhooks and Binance Futures API.
 */
@RestController
@RequestMapping("/api/v1/binance")
public class BinanceFuturesControllerImpl implements WebhookController<CoinData> {

    private final BinanceOrderService binanceOrderService;

    public BinanceFuturesControllerImpl(BinanceOrderService binanceOrderService) {
        this.binanceOrderService = binanceOrderService;
    }

    /**
     * Get information about current state of indicators for each coin.
     *
     * @return Map of ticker as a key and its local data as a value.
     */
    @GetMapping("/coins")
    public ResponseEntity<Map<String, CoinData>> getPositionsState() {
        var response = binanceOrderService.getCoinData();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/price")
    public ResponseEntity<String> getPrice(@RequestParam String code) {
        var response = binanceOrderService.getPrice(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //todo implement token check for Binance
    @Override
    public ResponseEntity<Object> checkToken(String request) {
        return null;
    }

    /**
     * Process request from TradingView in the following order: code, quantity, indicator, direction, price.
     * example: PENGUUSDT macd down
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
                .indicator(arr[1])
                .indicatorDirection(arr[2])
                .build();
    }
}

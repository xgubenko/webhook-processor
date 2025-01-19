package webhook.processor.service.impl;

import com.binance.connector.futures.client.impl.FuturesClientImpl;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webhook.processor.dto.BinancePriceDto;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;
import webhook.processor.properties.BinanceProperties;
import webhook.processor.service.BinanceOrderService;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class BinanceOrderServiceImpl implements BinanceOrderService {

    private final BinanceProperties properties;
    private final LocalDataService localDataService;
    private final TelegramBotServiceImpl telegramBotService;

    @Override
    public void process(String tvRequest) {
        var request = initRequest(tvRequest, properties.getBudget());
        log.info("Start processing request: {}", request);
        var coinData = localDataService.updateValue(request);

        var hullsuite = coinData.getHullsuite();
        var macd = coinData.getMacd();
        if (hullsuite != null && hullsuite.equals(macd)) {
            localDataService.removeCoin(coinData.getCode());
            try {
                createOrder(coinData);
            } catch (Exception e) {
                log.error("Request error: {}", e.getMessage());
            }
        }
    }

    @Override
    public void processTest(String tvRequest) {
        var request = initRequest(tvRequest, properties.getBudget());
        log.info("Start processing request: {}", request);
        var coinData = localDataService.updateValue(request);

        var hullsuite = coinData.getHullsuite();
        var macd = coinData.getMacd();
        var macdl = coinData.getMacdl();
        if (hullsuite != null && hullsuite.equals(macd) && hullsuite.equals(macdl)) {
            localDataService.removeCoin(coinData.getCode());
            try {
                createOrder(coinData);
            } catch (Exception e) {
                log.error("Request error: {}", e.getMessage());
            }
        }
    }

    @Override
    public Map<String, CoinData> getCoinData() {
        return localDataService.getStorage();
    }

    private void createOrder(CoinData coinData) {
        FuturesClientImpl client = new UMFuturesClientImpl(properties.getKey(), properties.getSecret());

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinData.getCode());

        var marketDirection = "BUY";
        if(coinData.getMacd().equals("down")) {
            marketDirection = "SELL";
        }
        parameters.put("side", marketDirection);
        parameters.put("type", "MARKET");
        parameters.put("quantity", coinData.getQuantity());

        log.info("Market order: {}", client.account().newOrder(parameters));

        parameters.put("type", "TRAILING_STOP_MARKET");

        var direction = "BUY";
        var price = 0.999;
        if(coinData.getMacd().equals("up")) {
            direction = "SELL";
            price = 1.001;
        }
        parameters.put("side", direction);
        parameters.put("callbackRate", properties.getTrailingDelta());
        parameters.put("timeInForce", "GTC");
        parameters.put("price", coinData.getPrice() * price);

        log.info("Stop loss order: {}", client.account().newOrder(parameters));
        telegramBotService.sendActionMessageToGroup(marketDirection, coinData.getCode(), coinData.getQuantity());
    }

    public BinancePriceDto getPrice(String code) {
        FuturesClientImpl client = new UMFuturesClientImpl(properties.getKey(), properties.getSecret());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", code);
        var mapper = new ObjectMapper();
        var response = client.market().tickerSymbol(parameters);

        try {
            return mapper.readValue(response, BinancePriceDto.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to get ticker price: {}", code);
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse TradingView webhook string into something usable.
     * @param s - webhook string
     * @return pojo to work with.
     */
    private BinanceTradingViewRequest initRequest(String s, Double budget) {
        var arr = s.split(" ");
        var price = Double.parseDouble(arr[3]);
        var quantity = Math.round(budget / price);

        return BinanceTradingViewRequest
                .builder()
                .code(arr[0])
                .indicator(arr[1])
                .indicatorDirection(arr[2])
                .price(price)
                .quantity(quantity)
                .build();
    }
}

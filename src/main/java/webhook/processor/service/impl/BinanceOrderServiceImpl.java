package webhook.processor.service.impl;

import com.binance.connector.futures.client.impl.FuturesClientImpl;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public void process(BinanceTradingViewRequest request) {
        log.info("Start processing request: {}", request);
        var coinData = localDataService.updateValue(request);

        var hullsuite = coinData.getHullsuite();
        var macd = coinData.getMacd();
        if (hullsuite != null && hullsuite.equals(macd)) {
            localDataService.removeCoin(coinData.getCode());
            createOrder(coinData);
        }
        localDataService.updateStorage();
    }

    @Override
    public Map<String, CoinData> getCoinData() {
        return localDataService.getStorage();
    }

    private void createOrder(CoinData coinData) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        FuturesClientImpl client = new UMFuturesClientImpl(properties.getKey(), properties.getSecret());

        parameters.put("symbol", coinData.getCode());

        var marketDirection = "BUY";
        if(coinData.getMacd().equals("down")) {
            marketDirection = "SELL";
        }
        parameters.put("side", marketDirection);
        parameters.put("type", "MARKET");
        parameters.put("quantity", coinData.getQuantity());

        System.out.println(client.account().newOrder(parameters));

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

        System.out.println(client.account().newOrder(parameters));
        telegramBotService.sendActionMessageToGroup(marketDirection, coinData.getCode(), String.valueOf(coinData.getQuantity()));
    }
}

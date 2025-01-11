package webhook.processor.service.impl;

import com.binance.connector.futures.client.impl.FuturesClientImpl;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.properties.BinanceProperties;
import webhook.processor.service.BinanceOrderService;

import java.util.LinkedHashMap;

@Service
@Slf4j
@AllArgsConstructor
public class BinanceOrderServiceImpl implements BinanceOrderService {

    private final BinanceProperties properties;

    @Override
    public void process(BinanceTradingViewRequest request) {
        log.info("Start processing request: {}", request);
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        FuturesClientImpl client = new UMFuturesClientImpl(properties.getKey(), properties.getSecret());

        System.out.println("MARKET");
        parameters.put("symbol", request.getCode());
        parameters.put("side", request.getDirection());
        parameters.put("type", "MARKET");
        parameters.put("quantity", request.getQuantity());

        System.out.println(client.account().newOrder(parameters));

        System.out.println("TRAILING STOP");
        parameters.put("type", "TRAILING_STOP_MARKET");

        var direction = "BUY";
        var price = request.getPrice() * 0.985;
        var formattedPriceString = String.valueOf(String.format("%.4f", price)).replace(',','.');
        parameters.put("side", direction);
        parameters.put("callbackRate", properties.getTrailingDelta());
        parameters.put("timeInForce", "GTC");
        parameters.put("price", formattedPriceString);

        System.out.println(client.account().newOrder(parameters));

        System.out.println("TAKE PROFIT:");
        price = request.getPrice() * 0.997;
        formattedPriceString = String.valueOf(String.format("%.4f", price)).replace(',','.');
        System.out.println(price);
        parameters.put("stopprice", formattedPriceString);
        parameters.put("type", "TAKE_PROFIT");
        System.out.println(client.account().newOrder(parameters));


    }
}

package webhook.processor.service.impl;

import lombok.extern.log4j.Log4j;
import org.jvnet.hk2.annotations.Service;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;

import java.util.HashMap;

@Log4j
@Service
public class LocalDataService {
    private final String HULLSUITE = "hullsuite";
    private final String MACD = "macd";

    private final HashMap<String, CoinData> storage = new HashMap<>();

    public CoinData updateValue(BinanceTradingViewRequest request) {
        var ticker = request.getCode();
        System.out.println("updating coin: " + ticker);
        var data = new CoinData();
        if (storage.containsKey(ticker)) {
            data = storage.get(ticker);
        }

        var indicator = request.getIndicator();
        var direction = request.getIndicatorDirection();
        var quantity = request.getQuantity();
        var price = request.getPrice();

        if (indicator.equals(HULLSUITE)) {
            data.setHullsuite(direction);
        }
        if (indicator.equals(MACD)) {
            data.setMacd(direction);
        }
        data.setQuantity(quantity);
        data.setCode(ticker);
        data.setPrice(price);

        storage.put(request.getCode(), data);
        System.out.println("Coin updated: " + data);

        return getCoinData(ticker);
    }

    public CoinData getCoinData(String ticker) {
        return storage.get(ticker);
    }
}
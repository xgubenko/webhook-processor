package webhook.processor.service;

import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;

import java.util.Map;

public interface BinanceOrderService {
    void process(BinanceTradingViewRequest request);

    Map<String, CoinData> getCoinData();
}

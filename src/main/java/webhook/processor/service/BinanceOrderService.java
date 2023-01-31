package webhook.processor.service;

import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.TradingViewRequest;

public interface BinanceOrderService {
    public void process(BinanceTradingViewRequest request);
}

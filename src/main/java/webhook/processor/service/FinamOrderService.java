package webhook.processor.service;

import webhook.processor.dto.TradingViewRequest;

public interface FinamOrderService {
    public void process(TradingViewRequest request);
}

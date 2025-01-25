package webhook.processor.service;

import webhook.processor.dto.BinancePriceDto;
import webhook.processor.dto.CoinData;

import java.util.Map;

public interface BinanceOrderService {
    void process(String request);
    void processTest(String request);

    Map<String, CoinData> getCoinData();

    void removeCoinFromStorage(String ticker);

    BinancePriceDto getPrice(String code);
}

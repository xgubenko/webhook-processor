package webhook.processor.service.impl;

import com.binance.connector.client.impl.SpotClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.service.BinanceOrderService;

import java.util.LinkedHashMap;

@Service
@Slf4j
public class BinanceOrderServiceImpl implements BinanceOrderService {

    private Boolean TRADING_IN_PROGRESS = false;
    @Override
    public void process(BinanceTradingViewRequest request) {
        log.info("Start processing request: {}", request);
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();

        SpotClientImpl client = new SpotClientImpl(request.getApi(), request.getSecret());

//                 Example params
//        parameters.put("symbol","BTCUSDT");
//        parameters.put("side", "SELL");
//        parameters.put("type", "LIMIT");
//        parameters.put("timeInForce", "GTC");
//        parameters.put("quantity", 0.01);
//        parameters.put("price", 9500);

        parameters.put("symbol", request.getCode());
        parameters.put("side", request.getDirection());
        parameters.put("type", "MARKET");
//        parameters.put("timeInForce", "GTC");

        double quantity = request.getQuantity();
        if(TRADING_IN_PROGRESS) quantity  = quantity * 2;
        parameters.put("quantity", quantity);

        String result = client.createTrade().testNewOrder(parameters);

        System.out.println(result);
    }
}

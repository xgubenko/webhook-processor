package webhook.processor.service.impl;

import org.springframework.web.client.RestTemplate;


//todo replace dependency client
public class BinanceFuturesClient {
    private static final String BASE_URL = "https://fapi.binance.com";
    private static final String PRODUCT = "/fapi";


    //todo https://developers.binance.com/docs/derivatives/usds-margined-futures/account/rest-api/Futures-Account-Balance-V3
    private void balanceRequest() {
        String s = new RestTemplate().getForObject(BASE_URL + PRODUCT, String.class);
    }
}

package webhook.processor.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import webhook.processor.dto.*;
import webhook.processor.service.FinamOrderService;

@Slf4j
@Service
public class FinamOrderServiceImpl implements FinamOrderService {

    @Override
    public void process(TradingViewRequest request) {
        log.info("Request processing started: {}", request);

        log.info("Creating order");
        NewOrder order = initOrder(request);
        log.info("Order created: {}", order);

        log.info("Sending new order request");
        String result = sendOrder(order, request);
        log.info("Order sent with result: {}", result);
    }

    @Override
    public String checkToken(TradingViewRequest request) {
        log.info("Check token start");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json-patch+json");
        headers.set("X-Api-Key", request.getApi());
        headers.set("accept", "text/plain");

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                "https://trade-api.comon.ru/api/v1/access-tokens/check", HttpMethod.GET, requestEntity, String.class).getBody();

        log.info("Response for token check: {}", response);

        return response;
    }

    private NewOrder initOrder(TradingViewRequest request) {
        NewOrder order = new NewOrder();
        order.setClientId(request.getClientId());

        //TQBR - акции мосбиржи, FUT- фьючерсы
        order.setBoard("TQBR");

        //SiH3
        order.setSecurityCode(request.getCode());
        order.setBuySell(request.getDirection());
        order.setQuantity(request.getQuantity());

        order.setPrice(null);
        order.setProperty(OrderPlacement.PutInQueue);

        OrderCondition condition = new OrderCondition(OrderConditionType.Bid, 0.0);
        order.setCondition(condition);

        OrderValidBefore validBefore = new OrderValidBefore(OrderValidBeforeType.TillEndSession);
        order.setValidBefore(validBefore);
        return order;
    }

    private String sendOrder(NewOrder order, TradingViewRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json-patch+json");
        headers.set("X-Api-Key", request.getApi());
        headers.set("accept", "text/plain");

        HttpEntity<NewOrder> newOrderHttpEntity = new HttpEntity<>(order, headers);

        restTemplate.postForEntity("https://trade-api.comon.ru/api/v1/orders", newOrderHttpEntity, NewOrder.class);
        return "OK";
    }
}

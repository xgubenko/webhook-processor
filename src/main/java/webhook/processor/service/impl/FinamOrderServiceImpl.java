package webhook.processor.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import webhook.processor.dto.*;
import webhook.processor.service.FinamOrderService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class FinamOrderServiceImpl implements FinamOrderService {

    private Boolean TRADING_IN_PROGRESS = false;

    @Override
    public void process(TradingViewRequest request) {
        log.info("Request processing started: {}", request);

        log.info("Creating order");
        NewOrder order = initOrder(request);
        log.info("Order created: {}", order);

        log.info("Sending new order request");
        String result = sendOrder(order, request);
        log.info("Order sent with result: {}", result);

        TRADING_IN_PROGRESS = true;
    }

    private NewOrder initOrder(TradingViewRequest request) {
        NewOrder order = new NewOrder();
        order.setClientId(request.getClientId());
        order.setBoard(null);
        order.setSecurityCode(request.getCode());
        order.setBuySell(request.getDirection());

        int quantity = request.getQuantity();
        if(TRADING_IN_PROGRESS) order.setQuantity(quantity * 2);
        else order.setQuantity(quantity);

        order.setPrice(null);
        order.setProperty(OrderPlacement.PutInQueue);

        LocalDateTime date = LocalDateTime.now();
        OrderCondition condition = new OrderCondition(OrderConditionType.Bid, null, date);
        order.setCondition(condition);

        OrderValidBefore validBefore = new OrderValidBefore(OrderValidBeforeType.TillEndSession, date);
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

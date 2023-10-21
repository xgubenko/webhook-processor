package webhook.processor.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import webhook.processor.dto.balance.BalanceData;
import webhook.processor.dto.balance.BalanceResponse;
import webhook.processor.dto.balance.Position;

import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
@Setter
@Service
public class TelegramBotServiceImpl extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    @Value("${bot.name}")
    private String username;
    @Value("${bot.token}")
    private String token;
    @Value("${bot.chatid}")
    private String chatId;
    @Value("${finam.host}")
    private String finamHost;
    @Value("${finam.key}")
    private String finamKey;
    @Value("${finam.id}")
    private String clientId;


    @PostConstruct
    private void post() {
        TelegramBotServiceImpl telegramBot = new TelegramBotServiceImpl();
        telegramBot.setUsername(username);
        telegramBot.setToken(token);
        telegramBot.setChatId(chatId);
        try {
            telegramBot.botConnect();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        String id = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage(id, text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void botConnect() throws TelegramApiException, ExecutionException, InterruptedException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Look for messages");

            SendMessage message = new SendMessage(chatId,
                    "Привет, кожаные ублюдки! Робот был перезапущен.");
            execute(message);

        } catch (TelegramApiException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }

    }

    public void sendMessageToGroup(String direction, String code, Integer amount) {
        SendMessage message = new SendMessage(chatId,
                String.format("Актив: %s \nНаправление: %s \nКоличество: %s", code, direction, amount));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 10,23 * * 1-5")
    private void scheduleTelegramNotifications() throws Exception {
        log.info("Get balance start");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json-patch+json");
        headers.set("X-Api-Key", finamKey);
        headers.set("accept", "text/plain");

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromHttpUrl(finamHost + "/public/api/v1/portfolio?Content.IncludeCurrencies=true&Content.IncludeMoney=true&" +
                        "Content.IncludePositions=true&Content.IncludeMaxBuySell=true")
                .queryParam("ClientId", clientId).encode().toUriString();

        BalanceResponse response = restTemplate
                .exchange(url
                        , HttpMethod.GET, requestEntity, BalanceResponse.class).getBody();
        log.info("Response for portfolio check: {}", response);

        BalanceData data = response.getData();

        StringBuilder messageText = new StringBuilder("Текущий баланс: " + data.getEquity());

        for (Position position : data.getPositions()) {
            messageText.append(String.format("\n\nТикер: %s. Размер позиции: %s\n" +
                            "Последняя цена: %s\n" +
                            "Средняя цена входа: %s\n" +
                            "Изменение счета по сделке: %s",
                    position.getSecurityCode(),
                    position.getBalance(),
                    position.getCurrentPrice(),
                    position.getAveragePrice(),
                    position.getUnrealizedProfit()));
        }

        SendMessage message = new SendMessage(chatId, messageText.toString());
        execute(message);
    }
}
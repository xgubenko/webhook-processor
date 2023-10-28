package webhook.processor.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
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
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import webhook.processor.dto.balance.BalanceData;
import webhook.processor.dto.balance.BalanceResponse;
import webhook.processor.dto.balance.Position;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
        log.info("onUpdateReceived called: {}", update);
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
        log.info("botConnect start");
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
        log.info("sendMessageToGroup call with parameters direction: {}, code: {}, amount: {}", direction, code, amount);

        String path = "src/main/resources/videos/usa.mp4";

        if (direction.equalsIgnoreCase("sell")) {
            path = "src/main/resources/videos/rus.mp4";
        }

        String caption = String.format("<b>Внимание! Проведена новая сделка.</b>" +
                "\n\nАктив: %s \nНаправление: %s \nКоличество: %s", code, direction, amount);

        try {
            sendMessageWithImage(path, caption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 10,23 * * 1-5")
    private void scheduleTelegramNotifications() throws Exception {
        log.info("Get balance start");
        BalanceResponse response = getPortfolioFromFinam();
        log.info("Response for portfolio check: {}", response);

        BalanceData data = response.getData();

        StringBuilder messageText = new StringBuilder("<b>Ежедневный отчет</b>" +
                "\n\nТекущий баланс: " + String.format("%.02f", data.getEquity())).append("₽");

        appendPositionsDescriptionIfTheyExist(data, messageText);

        SendMessage message = new SendMessage(chatId, messageText.toString());
        message.setParseMode("html");
        execute(message);
    }

    @Nullable
    private BalanceResponse getPortfolioFromFinam() {
        log.info("getPortfolioFromFinam start");

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
        return response;
    }

    @Scheduled(cron = "0 0 14 * * 6")
    private void scheduleTelegramProfitReport() throws Exception {
        log.info("scheduleTelegramProfitReport start");
        BalanceResponse response = getPortfolioFromFinam();
        log.info("Response for portfolio check: {}", response);

        BalanceData data = response.getData();

        Double initialSum = 45271.31;
        Double earnedRoubles = data.getEquity() - initialSum;
        Double earnedPercents = (data.getEquity() - initialSum) / (initialSum / 100);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
        String dateText = "21 10 2023";
        LocalDateTime dateStart = LocalDate.parse(dateText, dtf).atStartOfDay();
        Long dayCount = ChronoUnit.DAYS.between(dateStart, LocalDateTime.now());
        Double yearApproximateProfit = (earnedPercents / dayCount) * 365;

        StringBuilder messageText = new StringBuilder("<b>Еженедельный отчет</b>" +
                "\n\nТекущий баланс: " + String.format("%.02f", data.getEquity())).append("₽");
        messageText.append("\nБаланс на момент публикации робота: ").append(String.format("%.02f", initialSum)).append("₽");
        messageText.append("\nЗаработано: ").append(String.format("%.02f", earnedRoubles)).append("₽, ")
                .append(String.format("%.02f", earnedPercents)).append("%");
        messageText.append("\nОжидаемая годовая доходность: ").append(String.format("%.02f", yearApproximateProfit)).append("%");

        appendPositionsDescriptionIfTheyExist(data, messageText);

        SendMessage message = new SendMessage(chatId, messageText.toString());
        message.setParseMode("html");
        execute(message);
    }

    private static void appendPositionsDescriptionIfTheyExist(BalanceData data, StringBuilder messageText) {
        log.info("appendPositionsDescriptionIfTheyExist start, data: {}, messageText: {}", data, messageText);

        if (!data.getPositions().isEmpty()) {
            messageText.append("\n\nОткрытые позиции:");
        }

        for (Position position : data.getPositions()) {
            messageText.append(String.format("\n\nТикер: %s. Размер позиции: %s" +
                            "\nПоследняя цена: %s" +
                            "\nСредняя цена входа: %s" +
                            "\nДоходность сделки: %s",
                    position.getSecurityCode(),
                    position.getBalance(),
                    String.format("%.02f", position.getCurrentPrice()) + "₽",
                    String.format("%.02f", position.getAveragePrice()) + "₽",
                    String.format("%.02f", position.getUnrealizedProfit()) + "₽"));
        }
    }

    public void sendMessageWithImage(String path, String caption) throws Exception {
        log.info("imageSender start with path: {}, caption: {}", path, caption);

        try (InputStream inputStream = new FileInputStream(path)) {
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            SendVideo sendVideo = SendVideo.builder()
                    .chatId(String.valueOf(chatId))
                    .video(new InputFile(inputStream, fileName))
                    .caption(caption)
                    .build();
            sendVideo.setParseMode("html");

            execute(sendVideo);
        }
    }
}
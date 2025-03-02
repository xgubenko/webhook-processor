package webhook.processor.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
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

//    @Override
//    public void onUpdateReceived(Update update) {
//        log.info("onUpdateReceived called: {}", update);
//        String text = update.getMessage().getText();
//        String id = update.getMessage().getChatId().toString();
//        SendMessage message = new SendMessage(id, text);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasChannelPost() && update.getChannelPost().hasVideo()) {
            // Проверяем, является ли видео кругом (например, по разрешению)
            // Это условие нужно уточнить в зависимости от ваших требований
//            if (isCircleVideo(update.getMessage().getVideo())) {
                // Если видео круг, оставляем его
                return;
            } else {
                // Удаляем сообщение, если это не видео в формате кругов
                deleteMessage(update.getChannelPost().getChatId(), update.getChannelPost().getMessageId());
            }
//        } else if (update.hasMessage()) {
//            // Удаляем любое другое сообщение
//            deleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
//        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
//            execute(message);

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

    public void sendActionMessageToGroup(String direction, String code, Long amount) {
        log.info("sendMessageToGroup call with parameters direction: {}, code: {}, amount: {}", direction, code, amount);
        StringBuilder caption = new StringBuilder(String.format("Внимание! Проведена новая сделка." +
                "\n\nАктив: %s \nНаправление: %s \nКоличество: %s", code, direction, amount));
        try {
            SendMessage message = new SendMessage(chatId,
                    caption.toString());
            execute(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
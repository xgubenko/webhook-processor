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

    @PostConstruct
    private void post() {
        TelegramBotServiceImpl test_habr_bot = new TelegramBotServiceImpl();
        test_habr_bot.setUsername(username);
        test_habr_bot.setToken(token);
        test_habr_bot.setChatId(chatId);
        try {
            test_habr_bot.botConnect();
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
}
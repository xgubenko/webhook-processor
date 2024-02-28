package webhook.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import webhook.processor.dto.FinamTransactionDirection;
import webhook.processor.dto.TradingViewRequest;
import webhook.processor.properties.FinamProperties;
import webhook.processor.service.FinamOrderService;
import webhook.processor.service.impl.TelegramBotServiceImpl;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
@Component
@EnableScheduling
public class Mail {

    @Autowired
    TelegramBotServiceImpl telegramBotService;

    @Autowired
    FinamOrderService orderService;

    @Autowired
    FinamProperties finamProperties;

    @Value("${email}")
    private String email;

    @Value("${password}")
    private String password;

    @Value("${from}")
    private String tvEmail;

    @Scheduled(fixedDelay = 10000)
    private void scheduleMail() throws Exception {
        log.info("scheduleMail() was called");
        Store store = getSession().getStore();
        store.connect(email, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.getMessages();

        // print messages
        for (Message message : messages) {
            String from = Arrays.stream(message.getFrom()).toList().get(0).toString();
            log.info(from);
            if (from.contains(tvEmail)) {
                try {
                    log.info("Subject: {}", message.getSubject());
                    String messageText = message.getContent().toString().split("DELIMITER")[1];
                    log.info(messageText);
                    TradingViewRequest request = initRequest(messageText);

                    int counter = request.getQuantity();
                    request.setQuantity(1);
                    for (int i = 0; i < counter; i++) {
                        orderService.process(request);
                    }

                    telegramBotService.sendActionMessageToGroup(
                            request.getDirection().toString(), request.getCode(), counter);

                } catch (Exception e) {
                    log.warn(e.toString());
                }


            }
            message.setFlag(Flags.Flag.DELETED, true);
        }
//        log.info("Time: {}, number of messages received: {}", Instant.now(), messages.length);
        // close folder and store
        inbox.close(false);
        store.close();
    }

    private Session getSession() throws Exception {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        properties.put("mail.imaps.ssl.protocols", "TLSv1.2");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");

        return Session.getInstance(properties);
    }

    private TradingViewRequest initRequest(String s) {
        TradingViewRequest request = new TradingViewRequest();
        log.info("Time: {}, initRequest: {}", Instant.now(), s);
        String[] orderParams = s.split(" ");

        request.setClientId(finamProperties.getId());
        request.setApi(finamProperties.getKey());
        request.setCode(finamProperties.getCode());
        request.setQuantity(Integer.parseInt(orderParams[1]));

        if (orderParams[0].equals("buy")) request.setDirection(FinamTransactionDirection.Buy);
        else request.setDirection(FinamTransactionDirection.Sell);

        return request;
    }
}
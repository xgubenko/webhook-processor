package webhook.processor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bot")
public class TelegramProperties {
    private String name;
    private String token;
    private String chatid;
}

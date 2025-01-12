package webhook.processor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "binance")
@Data
public class BinanceProperties {
    private String key;
    private String secret;
    private Double trailingDelta;
}

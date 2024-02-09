package webhook.processor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "finam")
@Data
public class FinamProperties {
    private String host;
    private String key;
    private String id;
    private String code;
    private Integer quantity;
}

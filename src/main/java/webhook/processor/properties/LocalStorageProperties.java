package webhook.processor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "storage")
@Data
public class LocalStorageProperties {
    private String file;
}

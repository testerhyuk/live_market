package livemarket.videocall.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openvidu")
@Getter
@Setter
public class OpenViduProperties {
    private String url;
    private String secret;
}

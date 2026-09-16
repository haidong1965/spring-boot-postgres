package hd.webapp;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("server", java.util.Map.of(
            "currentTime", LocalDateTime.now().toString(),
            "timezone", java.time.ZoneId.systemDefault().getId()
        ));
        builder.withDetail("contact", "pan.peter1978@gmail.com");
        builder.withDetail("database", "PostgreSQL 18.4");
    }
}
package hd.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfig {

    // ✅ DirectChannel — synchronous, single subscriber, caller's thread
    @Bean(name = "logEventsChannel")
    public MessageChannel logEventsChannel() {
        return new DirectChannel();
    }
}
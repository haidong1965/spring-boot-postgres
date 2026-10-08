package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class AuthLoggingHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthLoggingHandler.class);

    @ServiceActivator(inputChannel = "logEventsChannel")
    public void handleAuth(Message<CustomSpringEvent> message) {
        CustomSpringEvent event = message.getPayload();
        String source = (String) message.getHeaders().get("source");
        System.out.println("xxxxxxxxxxxxxxxxxx AuthLoggingHandler::logEventsChannel:" + source + " xxxxxxxxxxxxxxxxxxxx");
        log.info("[DirectChannel] Log {}  received in thread {}",
            event.getSource(),
            Thread.currentThread().getName());
    }
}
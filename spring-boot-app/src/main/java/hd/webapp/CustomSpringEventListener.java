package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringEventListener {

	private static final Logger log = LoggerFactory.getLogger(CustomSpringEventListener.class);
    @EventListener
    public void handleCustomSpringEvent(CustomSpringEvent event) {
        log.info("++++++++ Received spring custom event : {} ++++++++", event) ;
    }
}

package hd.webapp;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringEventListener {

    @EventListener
    public void handleCustomSpringEvent(CustomSpringEvent event) {
        System.out.println("++++++++ Received spring custom event : " + event.getMessage() + " ++++++++") ;
    }
}

package hd.webapp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class LogEventChannelBridge {

	private final MessageChannel logEventsChannel;

	public LogEventChannelBridge(MessageChannel logEventsChannel) {
		this.logEventsChannel = logEventsChannel;
	}

	@EventListener
	public void bridge(CustomSpringEvent event) {
		Message<CustomSpringEvent> message = MessageBuilder.withPayload(event).setHeader("eventType", "Log")
				.setHeader("source", "AuthController").build();

		logEventsChannel.send(message);
	}
}

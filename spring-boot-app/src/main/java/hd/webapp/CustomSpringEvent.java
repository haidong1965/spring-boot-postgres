package hd.webapp;

import org.springframework.context.ApplicationEvent;

public class CustomSpringEvent extends ApplicationEvent {
    /**
	 * 
	 */
	private static final long serialVersionUID = -408878982167994418L;
	private final String message;

    public CustomSpringEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

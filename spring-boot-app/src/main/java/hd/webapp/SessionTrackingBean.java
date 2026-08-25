package hd.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionTrackingBean implements HttpSessionListener {

	private static final Logger log = LoggerFactory.getLogger(SessionTrackingBean.class);

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		log.info("Session created: {} at {}", se.getSession().getId(), se.getSession().getCreationTime());
	}

	@PostConstruct
	public void init() {
		log.info("********** @PostConstruct init() ***********");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.info("Session destroyed: {} lasted {} ms", se.getSession().getId(),
				System.currentTimeMillis() - se.getSession().getCreationTime());
	}

	@PreDestroy
	public void cleanup() {
		log.info("********** @PreDestroy cleanup() ***********");
	}
}

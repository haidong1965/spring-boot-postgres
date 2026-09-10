package hd.webapp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSessionTracker {
    
    private String sessionId;
    private String username;
    private LocalDateTime loginTime;
    
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    
    public void registerSession(String sessionId, String username, String ip, String userAgent) {
        sessions.put(sessionId, new SessionInfo(
            sessionId, username, ip, userAgent,
            Instant.now(), true
        ));
    }

    public void unregisterSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public List<SessionInfo> getAllActiveSessions() {
        return new ArrayList<>(sessions.values());
    }

    public boolean isValid(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        return info != null && info.isActive();
    }

    public void forceLogout(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        if (info != null) {
            info.setActive(false);
        }
    }

    public void forceLogoutByUsername(String username) {
        sessions.values().stream()
            .filter(s -> s.getUsername().equals(username))
            .forEach(s -> s.setActive(false));
    }
}

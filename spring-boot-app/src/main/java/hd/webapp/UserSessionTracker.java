package hd.webapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSessionTracker {
    
    private String sessionId;
    private String username;
    private LocalDateTime loginTime;
    private List<String> activityLog = new ArrayList<>();
    
    public void logActivity(String activity) {
        activityLog.add(LocalDateTime.now() + " - " + activity);
    }
    
    // Getters and setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
    
    public List<String> getActivityLog() { return activityLog; }
}

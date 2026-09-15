package hd.webapp;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfo {
    private String sessionId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private Instant loginTime;
    private boolean active;
}
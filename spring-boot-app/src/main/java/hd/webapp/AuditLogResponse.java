package hd.webapp;

import java.time.OffsetDateTime;

//Used for outgoing GET responses
public record AuditLogResponse(Long id, String message, OffsetDateTime auditDate, AuditLevel auditLevel) {
}

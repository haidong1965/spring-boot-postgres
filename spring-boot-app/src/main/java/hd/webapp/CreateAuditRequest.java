package hd.webapp;

public record CreateAuditRequest(String message, AuditLevel auditLevel) {}

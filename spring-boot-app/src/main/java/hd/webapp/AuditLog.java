package hd.webapp;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Getter
@Entity
@Table(name = "audit")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // OffsetDateTime is the correct Java type for PostgreSQL's TIMESTAMPTZ
    @Column(name = "audit_date", nullable = false)
    private OffsetDateTime auditDate;

    @Enumerated(EnumType.STRING) // Stores as "INFO", "ERROR", etc.
    @Column(name = "auditlevel", nullable = false, length = 50)
    private AuditLevel auditLevel;

    // Default constructor required by JPA
    protected AuditLog() {}

    public AuditLog(String message, AuditLevel auditLevel) {
        this.message = message;
        this.auditLevel = auditLevel;
        this.auditDate = OffsetDateTime.now(); // Set timestamp upon creation
    }
}
package hd.webapp;

import java.util.List;
import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditLog, Long>{
    // Spring Data automatically generates the query based on the method name
    List<AuditLog> findByAuditLevel(AuditLevel auditLevel);

    // Example of a custom query using the date index
    List<AuditLog> findByAuditDateBetween(OffsetDateTime start, OffsetDateTime end);
}

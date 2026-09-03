package hd.webapp;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    // Constructor injection (Recommended over @Autowired on fields)
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLogResponse createAudit(CreateAuditRequest request) {
        AuditLog log = new AuditLog(request.message(), request.auditLevel());
        
        // Save returns the managed entity (now in the L1 Cache with an ID)
        AuditLog savedLog = auditRepository.save(log); 
        
        return mapToResponse(savedLog);
    }

    @Transactional(readOnly = true) // Optimizes the Hibernate Session for read-only operations
    public List<AuditLogResponse> getAllAudits() {
        return auditRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditsByLevel(AuditLevel level) {
        return auditRepository.findByAuditLevel(level).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getMessage(),
                log.getAuditDate(),
                log.getAuditLevel()
        );
    }
}

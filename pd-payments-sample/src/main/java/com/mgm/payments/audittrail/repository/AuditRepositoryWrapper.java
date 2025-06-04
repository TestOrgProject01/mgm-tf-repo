package com.mgm.payments.audittrail.repository;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.mgm.payments.audittrail.AuditTrailConstants;
import com.mgm.payments.audittrail.model.AuditTrailRequest;

@Component
@Retryable(value = { Exception.class }, maxAttempts = AuditTrailConstants.MAX_RETRY, backoff = @Backoff(AuditTrailConstants.DELAY_SECONDS * 1000))
public class AuditRepositoryWrapper {
    private final Logger logger = LoggerFactory.getLogger(AuditRepositoryWrapper.class);
    private final AuditRepository auditRepository;

    public AuditRepositoryWrapper(AuditRepository auditRepository) {
        super();
        this.auditRepository = auditRepository;
    }

    @Recover
    public void recover(Exception e) throws Exception {
        logger.warn("Maximum AuditTrail Database Retries Reached !!!");
        logger.info("Exception during Audittrail DB operation", e);
        throw e;

    }

    public Optional<AuditTrailRequest> save(AuditTrailRequest auditTrailRequest) {
        return Optional.of(auditRepository.save(auditTrailRequest));

    }

}

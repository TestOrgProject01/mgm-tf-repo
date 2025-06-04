package com.mgm.payments.audittrail.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.mgm.payments.audittrail.model.AuditTrailRequest;

import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends CosmosRepository<AuditTrailRequest, String> {
}

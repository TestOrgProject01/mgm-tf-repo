package com.mgm.payments.audittrail;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgm.payments.audittrail.model.AuditTrailRequest;
import com.mgm.payments.audittrail.model.TriggerRequest;
import com.mgm.payments.audittrail.repository.AuditRepositoryWrapper;

@Component
public class AuditTrailQueueTrigger implements Function<TriggerRequest, String> {
    @Autowired
    private AuditRepositoryWrapper auditRepo;

    public String apply(TriggerRequest triggerRequest) {
        triggerRequest.getContext().getLogger().info("Entered:Audit details Save method:");
        String request = triggerRequest.getMessage();
        ObjectMapper objectMapper = new ObjectMapper();
        AuditTrailRequest auditrequest = null;
        try {
            auditrequest = objectMapper.readValue(request, AuditTrailRequest.class);
            auditRepo.save(auditrequest);
            triggerRequest.getContext().getLogger().info("Audit details succesfully saved");
        } catch (JsonProcessingException e) {
            triggerRequest.getContext().getLogger().info("Failed to Save Request");
            triggerRequest.getContext().getLogger().info(e.toString());
        } catch (Exception e) {
            triggerRequest.getContext().getLogger().info("Exception during Audittrail DB operation");
            triggerRequest.getContext().getLogger().info(e.toString());
            triggerRequest.getContext().getLogger().info("Java Queue trigger function failed to  processed a message: " + auditrequest.getCorrelationId()+auditrequest.getAuditData().getEventName());
        }finally {
        	 triggerRequest.getContext().getLogger().info("Java Queue trigger function processed a message: " + auditrequest.getId());
        }
        return null;
    }

}

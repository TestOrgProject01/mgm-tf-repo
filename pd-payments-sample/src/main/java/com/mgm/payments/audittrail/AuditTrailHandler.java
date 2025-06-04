package com.mgm.payments.audittrail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mgm.payments.audittrail.model.TriggerRequest;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

@Component
public class AuditTrailHandler {

    @Autowired
    private AuditTrailQueueTrigger auditTrailQueueTrigger;

    @FunctionName("AuditTrailQueueTrigger")
    public void execute(@QueueTrigger(name = "message", queueName = "audittrailstoragequeue", connection = "AzureWebJobsStorage") String message,
            ExecutionContext context) {
        try {
            context.getLogger().info("In AuditTrailQueueTrigger function message ");
            TriggerRequest triggerrequest = TriggerRequest.builder().message(message).context(context).build();
            auditTrailQueueTrigger.apply(triggerrequest);
        } catch (Exception e) {
            context.getLogger().info("FAILURE :Exception Occured while saving Audit Record");
            context.getLogger().info(e.toString());

        } finally {
            context.getLogger().info("Queue trigger function processed a message ");
        }
    }
}

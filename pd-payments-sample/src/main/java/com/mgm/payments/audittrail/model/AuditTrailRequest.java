package com.mgm.payments.audittrail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Container(containerName = "payments-audit-trail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTrailRequest {

    @Id
    private String id;
    private String topic;
    private String subject;
    private String eventType ;
    private String eventTime;
    private String clientReferenceNumber;
    private String sessionId;
    private String mgmId;
    private String clientId;
    private String journeyId;
    private String executionId;
    private String correlationId;
    private String transactionId;
    private String requestId;
    private String gatewayChainId;
    private String mgmChannel;
    private String createdDate;
    private String dataVersion ;
    private AuditData auditData;
    private String cardEntryMode;

}

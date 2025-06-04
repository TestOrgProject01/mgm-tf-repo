package com.mgm.payments.audittrail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditData {

    private String eventName;
    private String eventDescription;

    private String status;

    private String startTimeTS;

    private String endTimeTS;

    private String serviceName;

    private String mgmErrorCode;

    private String externalErrorCode;

    private String errorDescription;
    private String subject;
    private String createdDate;
    private Object requestPayload;
    private Object responsePayload;

    private String gateWayId;
    private String lastFour;
    private String mgmToken;
    private String processorToken;
    private String result;

}

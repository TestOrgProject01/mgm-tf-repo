package com.mgm.payments.audittrail.model;

import com.microsoft.azure.functions.ExecutionContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerRequest {
 private String message;
 private ExecutionContext context;
}

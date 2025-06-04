package com.mgm.payments.audittrail;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mgm.payments.audittrail.model.TriggerRequest;
import com.mgm.payments.audittrail.repository.AuditRepositoryWrapper;
import com.microsoft.azure.functions.ExecutionContext;
@ExtendWith(MockitoExtension.class)
public class AuditTrailQueueTriggerTests {
	@Mock
	AuditRepositoryWrapper auditRepo;
	@Mock
	 ExecutionContext context;
	@InjectMocks
	AuditTrailQueueTrigger queueTrigger;
	
	@Test
	void testApply() {
		String message="{\r\n"
				+ "    \"id\": \"1b7058fe-d71d-4eb9-9b24-f0a99293a943\",\r\n"
				+ "    \"topic\": \"mgmAuditTrailTopic\",\r\n"
				+ "    \"subject\": \"Audit Trail\",\r\n"
				+ "    \"eventType\": \"mgm.payments.audittrail\",\r\n"
				+ "    \"eventTime\": \"2023-12-06T07:35:20.956978900\",\r\n"
				+ "    \"orderId\": \"\",\r\n"
				+ "    \"sessionId\": \"\",\r\n"
				+ "    \"mgmId\": \"\",\r\n"
				+ "    \"clientId\": \"client123\",\r\n"
				+ "    \"journeyId\": \"x-mgm-journey-id\",\r\n"
				+ "    \"executionId\": \"executionId\",\r\n"
				+ "    \"coorelationId\": \"x-mgm-correlation-id\",\r\n"
				+ "    \"transactionId\": \"mgmTransactionId1\",\r\n"
				+ "    \"mgmChannel\": \"mgmChannel1\",\r\n"
				+ "    \"createdDate\": \"2023-12-06T07:35:20.941373\",\r\n"
				+ "    \"dataVersion\": \"0.1\",\r\n"
				+ "    \"auditData\": {\r\n"
				+ "        \"eventName\": \"Tokenization\",\r\n"
				+ "        \"eventDescription\": \"MGM Token inserted into Token DB\",\r\n"
				+ "        \"status\": \"Success\",\r\n"
				+ "        \"startTimeTS\": \"2023-12-06T07:35:20.9257442\",\r\n"
				+ "        \"endTimeTS\": \"2023-12-06T07:35:20.9257442\",\r\n"
				+ "        \"mgmErrorCode\": \"\",\r\n"
				+ "        \"externalErrorCode\": \"\",\r\n"
				+ "        \"errorDescription\": \"\",\r\n"
				+ "        \"subject\": \"\",\r\n"
				+ "        \"createdDate\": \"2023-12-06T07:35:20.941373\",\r\n"
				+ "        \"gateWayId\": \"\",\r\n"
				+ "        \"lastFour\": \"\",\r\n"
				+ "        \"mgmToken\": \"581798761919509\"\r\n"
				+ "    }\r\n"
				+ "}";

       ExecutionContext context =  Mockito.mock(ExecutionContext.class);
       Logger logger = Mockito.mock(Logger.class);
       when(context.getLogger()).thenReturn(logger);
       doNothing().when(logger).info(anyString());
		TriggerRequest request=new TriggerRequest();
		request.setContext(context);
		request.setMessage(message);
		assertNull(queueTrigger.apply(request));
	}

}

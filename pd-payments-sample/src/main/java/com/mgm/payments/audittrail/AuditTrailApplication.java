package com.mgm.payments.audittrail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry(proxyTargetClass = true)
public class AuditTrailApplication {

    public static void main(String[] args)  {
        SpringApplication.run(AuditTrailApplication.class, args);
    }
   
}

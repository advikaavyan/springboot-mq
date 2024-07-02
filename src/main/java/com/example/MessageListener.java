package com.example;

import com.example.service.RecordService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageListener {

    private final JmsTemplate jmsTemplate;
    private final RecordService recordService; // Service to handle validation, enrichment, and persistence

    public MessageListener(JmsTemplate jmsTemplate, RecordService recordService) {
        this.jmsTemplate = jmsTemplate;
        this.recordService = recordService;
    }

   /* @JmsListener(destination = "upstreamQueue")
    @Transactional
    public void receiveMessage(String message) {
        try {
            // Validate and enrich the message
            RecordEntity record = recordService.validateAndEnrich(message);

            // Persist the record
            recordService.save(record);

            // Publish to downstream queue
            jmsTemplate.convertAndSend("downstreamQueue", record);

        } catch (Exception e) {
            // Handle unhandled exceptions and rollback
            throw new RuntimeException("Error processing message", e);
        }
    }*//* @JmsListener(destination = "upstreamQueue")
    @Transactional
    public void receiveMessage(String message) {
        try {
            // Validate and enrich the message
            RecordEntity record = recordService.validateAndEnrich(message);

            // Persist the record
            recordService.save(record);

            // Publish to downstream queue
            jmsTemplate.convertAndSend("downstreamQueue", record);

        } catch (Exception e) {
            // Handle unhandled exceptions and rollback
            throw new RuntimeException("Error processing message", e);
        }
    }*/
}

package com.example;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BackOutQueueListener {

    private final RecordMQService recordService;

    public BackOutQueueListener(RecordMQService recordService) {
        this.recordService = recordService;
    }

    @JmsListener(destination = "backOutQueue")
    public void processBackOutMessage(RecordEntity record) {
        recordService.handleMessageRetry(record);
    }
}

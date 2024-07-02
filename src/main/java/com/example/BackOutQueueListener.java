package com.example;

import com.example.service.MessageProcessingService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BackOutQueueListener {

/*    private final MessageProcessingService recordService;

    public BackOutQueueListener(MessageProcessingService recordService) {
        this.recordService = recordService;
    }

    @JmsListener(destination = "backOutQueue")
    public void processBackOutMessage(RecordEntity record) {
        recordService.handleMessageRetry(record);
    }*/
}

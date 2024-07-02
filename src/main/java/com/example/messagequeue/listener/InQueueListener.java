package com.example.messagequeue.listener;

import com.example.RecordEntity;
import com.example.RecordMQService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InQueueListener {

    private final JmsTemplate jmsTemplateOutbound;
    private final RecordMQService recordService;
    @Value("${messagequeue.outbound.queueName}")
    private String queueName;


    public InQueueListener(@Qualifier("jmsTemplateOutbound") JmsTemplate jmsTemplateOutbound, RecordMQService recordService) {
        this.jmsTemplateOutbound = jmsTemplateOutbound;
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAa=====jmsTemplateOutboundjmsTemplateOutboundjmsTemplateOutboundjmsTemplateOutboundjmsTemplateOutbound=============" + jmsTemplateOutbound);
        this.recordService = recordService;
    }

    @JmsListener(destination = "${messagequeue.inbound.queueName}", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void receiveMessage(String message) {
        try {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAa==================" + message);
            RecordEntity record = recordService.validateAndEnrich(message);

            jmsTemplateOutbound.convertAndSend(queueName, "'{\"name\":\"John\", \"age\":30, \"car\":null}'");
            System.out.println("ALLL GOOD ==================");
        } catch (Exception e) {
            throw new RuntimeException("Error processing message", e);
        }
    }
}

package com.example.messagequeue.listener;

import com.example.dto.IncomingMessageData;
import com.example.exception.SomeApiException;
import com.example.service.MessageProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class InternalQueueListener {

    private final JmsTemplate jmsTemplateOutbound;
    private final JmsTemplate jmsTemplateInternal;

    private final MessageProcessingService recordService;
    @Value("${messagequeue.internal.backoutQueueName}")
    private String backoutQueueName;

    @Value("${messagequeue.internal.deadletterQueueName}")
    private String deadletterQueueName;


    public InternalQueueListener(@Qualifier("jmsTemplateOutbound") JmsTemplate jmsTemplateOutbound, @Qualifier("jmsTemplateInternal") JmsTemplate jmsTemplateInternal, MessageProcessingService recordService) {
        this.jmsTemplateOutbound = jmsTemplateOutbound;
        this.jmsTemplateInternal = jmsTemplateInternal;
        this.recordService = recordService;
    }

    int counter = 0;

    @JmsListener(destination = "${messagequeue.internal.backoutQueueName}", containerFactory = "jmsListenerContainerFactoryInternal")
    @Transactional
    public void backoutQueueNameMessageReceive(IncomingMessageData message) throws SomeApiException {

        counter++;
        IncomingMessageData record = null;
        try {
            log.info("Message received from the queue {} and message is \n{}",  backoutQueueName, message);
            recordService.handleMessageRetry(message);

            //  jmsTemplateOutbound.convertAndSend(deadletterQueueName, "'{\"name\":\"John\", \"age\":30, \"car\":null}'");
            log.info("ALLL GOOD ==================");
        } catch (Exception e) {
            log.info("backoutQueueNameMessageReceive    ==Exception================ {}");
            jmsTemplateInternal.convertAndSend(deadletterQueueName, record.getData() + "");
            //throw new RuntimeException("Error processing message", e);
        }
    }
}

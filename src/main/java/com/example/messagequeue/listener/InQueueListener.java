package com.example.messagequeue.listener;

import com.example.dto.OutgoingMessageData;
import com.example.exception.BackoutException;
import com.example.service.MessageProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class InQueueListener {

    private final JmsTemplate jmsTemplateOutbound;
    private final JmsTemplate jmsTemplateInternal;
    private final MessageProcessingService recordService;
    @Value("${messagequeue.inbound.queueName}")
    private String queueNameInbound;

    @Value("${messagequeue.internal.backoutQueueName}")
    private String backoutQueueName;

    @Value("${messagequeue.outbound.queueName}")
    private String queueNameOutbound;


    public InQueueListener(@Qualifier("jmsTemplateOutbound") JmsTemplate jmsTemplateOutbound, @Qualifier("jmsTemplateInternal") JmsTemplate jmsTemplateInternal, MessageProcessingService recordService) {
        this.jmsTemplateOutbound = jmsTemplateOutbound;
        this.jmsTemplateInternal = jmsTemplateInternal;
        this.recordService = recordService;
    }

    int counter = 0;

    @JmsListener(destination = "${messagequeue.inbound.queueName}", containerFactory = "jmsListenerContainerFactoryInbound")
    @Transactional
    public void receiveMessage(String message) throws BackoutException {
        counter++;
        OutgoingMessageData outgoingMessageData = null;
        try {
            log.info("Message received from the queue '{}' and message is \n {}", queueNameInbound, message);
            // Thread.sleep(5000);
            outgoingMessageData = recordService.validate(message);
            log.info("Message ready to publish to queue {} ", queueNameOutbound, message);
            jmsTemplateOutbound.convertAndSend(queueNameOutbound, getJson(outgoingMessageData.getData()));
            log.info("All Good Message committed ============");
        } catch (BackoutException e) {
            log.info("Message couldn't processed due to {} so now system is sending the incomingMessageData object to queue {} and object is {} ", e.getMessage(), backoutQueueName, e.getIncomingMessageData());
            jmsTemplateInternal.convertAndSend(backoutQueueName, e.getIncomingMessageData());
        } catch (Exception e) {
            log.info("Message getting rolled back to queue '{}' due to run time exception {}", queueNameInbound, e.getMessage());
            throw new RuntimeException("Message getting rolled back from due to run time exception {} ", e);
        }
    }

    public String getJson(String message) {


        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";

        try {
            // Convert the Emp object to a JSON string
            jsonString = objectMapper.writeValueAsString(message);
            System.out.println(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}

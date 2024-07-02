package com.example.service;


import com.example.dto.IncomingMessageData;
import com.example.RecordRepository;
import com.example.dto.OutgoingMessageData;
import com.example.exception.SomeApiException;
import com.example.exception.ApiCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessingService {

    private final RecordRepository recordRepository = null;
    private final JmsTemplate jmsTemplateInbound;
    private final JmsTemplate jmsTemplateOutbound;
    private final JmsTemplate jmsTemplateInternal;
    @Value("${messagequeue.internal.backoutQueueName}")
    private String backoutQueueName;

    @Autowired
    ApiService apiService;
    public MessageProcessingService(@Qualifier("jmsTemplateInbound") JmsTemplate jmsTemplateInbound, @Qualifier("jmsTemplateOutbound") JmsTemplate jmsTemplateOutbound, @Qualifier("jmsTemplateInternal") JmsTemplate jmsTemplateInternal) {
       // this.recordRepository = recordRepository;
        this.jmsTemplateInbound = jmsTemplateInbound;
        this.jmsTemplateOutbound = jmsTemplateOutbound;
        this.jmsTemplateInternal = jmsTemplateInternal;
    }
    public OutgoingMessageData validate(String message)  throws ApiCallException {
        IncomingMessageData incomingMessageData = new IncomingMessageData();
        incomingMessageData.setData(message);
        OutgoingMessageData outgoingMessageData = apiService. processMessage(incomingMessageData);
        return outgoingMessageData;
    }
    @Retryable(
            value = { SomeApiException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    public void callExternalApi(IncomingMessageData record) throws ApiCallException {

        apiService. processMessage(record);
       /* boolean someConditionFails = true;
        // Call external API and handle logic
        System.out.println("API CALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLll" +jmsTemplateInternal);
        if (someConditionFails) {
            System.out.println("Going to send message to backout queue");
           // jmsTemplateInternal.convertAndSend(backoutQueueName, record);
             throw new SomeApiException("API call failed");
        }*/
    }

    public void handleMessageRetry(IncomingMessageData record) {
        System.out.println("========handleMessageRetryhandleMessageRetryhandleMessageRetryhandleMessageRetryhandleMessageRetry==");
       /* try {
          //  callExternalApi(record);
            apiService. processMessage("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaa");
            jmsTemplateOutbound.convertAndSend("downstreamQueue", record);
        } catch (SomeApiException e) {
            record.setRetryCount(record.getRetryCount() + 1);
            if (record.getRetryCount() >= 10) {
                jmsTemplateInternal.convertAndSend("deadLetterQueue", record);
            } else {
                jmsTemplateInternal.convertAndSend("backOutQueue", record);
            }
        }*/
    }
}

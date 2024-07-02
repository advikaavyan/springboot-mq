package com.example;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RecordMQService {

    private final RecordRepository recordRepository = null;
    private final JmsTemplate jmsTemplateInbound;
    private final JmsTemplate jmsTemplateOutbound;
    private final JmsTemplate jmsTemplateInternal;

    public RecordMQService(  @Qualifier("jmsTemplateInbound") JmsTemplate jmsTemplateInbound,  @Qualifier("jmsTemplateOutbound") JmsTemplate jmsTemplateOutbound,  @Qualifier("jmsTemplateInternal") JmsTemplate jmsTemplateInternal) {
       // this.recordRepository = recordRepository;
        this.jmsTemplateInbound = jmsTemplateInbound;
        this.jmsTemplateOutbound = jmsTemplateOutbound;
        this.jmsTemplateInternal = jmsTemplateInternal;
    }
    public RecordEntity validateAndEnrich(String message) {
        // Validation and enrichment logic
        RecordEntity record = new RecordEntity();
        record.setData(message);
        return record;
    }
    @Retryable(
            value = { SomeApiException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    public void callExternalApi(RecordEntity record) throws SomeApiException {
        boolean someConditionFails = true;
        // Call external API and handle logic
        if (someConditionFails) {
            throw new SomeApiException("API call failed");
        }
    }

    public void handleMessageRetry(RecordEntity record) {
        try {
            callExternalApi(record);
            jmsTemplateOutbound.convertAndSend("downstreamQueue", record);
        } catch (SomeApiException e) {
            record.setRetryCount(record.getRetryCount() + 1);
            if (record.getRetryCount() >= 10) {
                jmsTemplateInternal.convertAndSend("deadLetterQueue", record);
            } else {
                jmsTemplateInternal.convertAndSend("backOutQueue", record);
            }
        }
    }
}

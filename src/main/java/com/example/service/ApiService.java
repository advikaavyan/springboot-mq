package com.example.service;

import com.example.dto.IncomingMessageData;
import com.example.dto.OutgoingMessageData;
import com.example.exception.ApiCallException;
import com.example.exception.BackoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Slf4j
public class ApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    int counter = 0;

    private static final String API_URL = "http://localhost:9996/actuator/hawtio/activemq/sendMessage?nid=root-org.apache.activemq-Broker-localhost-Queue-IN.TO.FAB";

    public OutgoingMessageData processMessage(IncomingMessageData incomingMessageData) throws BackoutException {
        log.info("processMessage received incomingMessageData  \n{} ", incomingMessageData);
        String message = incomingMessageData.getData();
        RetryTemplate retryTemplate = getRetryTemplate();
        counter++;
        try {
            // Execute the retryable code
            retryTemplate.execute(new RetryCallback<Void, Exception>() {
                @Override
                public Void doWithRetry(RetryContext context) throws Exception {
                    log.info("Going to make API call {} time ", context.getRetryCount() + 1);
                    makeApiCall(message);
                    return null;
                }
            });
        } catch (ApiCallException e) {
            log.info("ApiCallException occurred after 3 reties so now message should go to backout queue");
            throw new BackoutException(incomingMessageData, e);
        } catch (Exception e) {
            log.info("RuntimeException occurred after 3 reties so now message should go back to source queue");
            throw new RuntimeException(e);
        }

        OutgoingMessageData outgoingMessageData = new OutgoingMessageData();
        outgoingMessageData.setData(incomingMessageData.getData() +"   READY "+ new Date());
        return  outgoingMessageData;
    }

    private RetryTemplate getRetryTemplate() {
        // Create a RetryTemplate instance
        RetryTemplate retryTemplate = new RetryTemplate();

        // Configure retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Configure backoff policy
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 2 seconds
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

   /* private void makeApiCall(String message) throws Exception {
        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        throw new Exception("API call failed");
       *//* ResponseEntity<String> response = restTemplate.postForEntity(API_URL, message, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("API call failed");
        }*//*
    }*/
   private void makeApiCall(String message) throws ApiCallException {
       if(message.startsWith("1")) {
           throw new RuntimeException("API call failed with status code: " + "Service Not found");
       } else if(message.startsWith("2")) {
           throw new ApiCallException("API call failed with status code: " + "500");
       }else if(message.startsWith("3")) {
           log.info("No issue... API worked well");
       }



   }


   /* private void makeApiCall(String message) throws ApiCallException {
        if(message.startsWith("1"))
        if (counter / 2 == 0) {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, message, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ApiCallException("API call failed with status code: " + response.getStatusCode());
            }
        } else {
            throw new ApiCallException("API call failed with status code: " + "500");
        }

    }*/

    private void sendToBackoutQueue(String message) {
        // Implement sending to backout queue
        System.out.println("Sending message to backout queue: " + message);
    }
}

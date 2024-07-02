package com.example.messagequeue.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
@EnableJms
public class InternalJmsConfig {

    @Value("${messagequeue.internal.brokerURI}")
    private String brokerURI;
    @Value("${messagequeue.internal.brokerUserName}")
    private String brokerUsername;
    @Value("${messagequeue.internal.brokerPassword}")
    private String brokerPassword;

    @Value("${messagequeue.internal.backoutQueueName}")
    private String backoutQueueName;
    @Value("${messagequeue.internal.deadletterQueueName}")
    private String deadletterQueueName;

    @Bean(name = "connectionFactoryInternal")
    public ConnectionFactory connectionFactoryInternal() {
        System.out.println("==============brokerURI   ==" + brokerURI);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerURI);
        factory.setUserName(brokerUsername);
        factory.setPassword(brokerPassword);
        return factory;
    }

    @Bean(name = "jmsTemplateInternal")
    public JmsTemplate jmsTemplateInternal(ConnectionFactory connectionFactoryInternal) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryInternal);
        jmsTemplate.setSessionTransacted(true);
        System.out.println("jmsTemplateInternal=="+jmsTemplate);
        return jmsTemplate;
    }

    @Bean(name = "jmsListenerContainerFactoryInternal")
    protected DefaultJmsListenerContainerFactory jmsListenerContainerFactoryInternal(ConnectionFactory connectionFactoryInternal) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactoryInternal);
        // factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> {
            // Add error handling logic here
        });
        return factory;
    }
}

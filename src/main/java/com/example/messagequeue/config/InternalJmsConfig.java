package com.example.messagequeue.config;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;


@Configuration
@EnableJms
@Slf4j
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
        log.info("==============brokerURI   ==" + brokerURI);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerURI);
        factory.setUserName(brokerUsername);
        factory.setPassword(brokerPassword);
        return factory;
    }

    @Bean(name = "jmsTemplateInternal")
    @Qualifier("jmsTemplateInternal")
    public JmsTemplate jmsTemplateInternal(@Qualifier("connectionFactoryInternal") ConnectionFactory connectionFactoryInternal) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryInternal);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        log.info("jmsTemplateInternal==" + jmsTemplate);
        return jmsTemplate;
    }

    @Bean(name = "jmsListenerContainerFactoryInternal")
    protected DefaultJmsListenerContainerFactory jmsListenerContainerFactoryInternal(@Qualifier("connectionFactoryInternal") ConnectionFactory connectionFactoryInternal) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactoryInternal);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        // factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> {
            // Add error handling logic here
        });
        return factory;
    }

    // @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}

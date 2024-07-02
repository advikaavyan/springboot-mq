package com.example.messagequeue.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
@EnableJms
public class IncomingJmsConfig {

    @Value("${messagequeue.inbound.brokerURI}")
    private String brokerURI;
    @Value("${messagequeue.inbound.brokerUserName}")
    private String brokerUsername;
    @Value("${messagequeue.inbound.brokerPassword}")
    private String brokerPassword;

    @Value("${messagequeue.inbound.queueName}")
    private String queueName;

    @Bean(name = "connectionFactoryInbound")
    @Primary
    public ConnectionFactory connectionFactoryInbound() {
        System.out.println("==============brokerURI   ==" + brokerURI);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerURI);
        factory.setUserName(brokerUsername);
        factory.setPassword(brokerPassword);
        return factory;
    }

    @Bean(name = "jmsTemplateInbound")
    @Qualifier("jmsTemplateInbound")
    @Primary
    public JmsTemplate jmsTemplateInbound(@Qualifier("connectionFactoryInbound") ConnectionFactory connectionFactoryInbound) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryInbound);
        jmsTemplate.setSessionTransacted(true);
        System.out.println("jmsTemplateInbound=="+jmsTemplate);
        return jmsTemplate;
    }

    @Bean(name = "jmsListenerContainerFactoryInbound")
    @Primary
    protected DefaultJmsListenerContainerFactory jmsListenerContainerFactoryInbound(@Qualifier("connectionFactoryInbound") ConnectionFactory connectionFactoryInbound) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactoryInbound);
        // factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> {
            // Add error handling logic here
        });
        return factory;
    }


}

package com.example.messagequeue.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;


@Configuration
@EnableJms
public class OutJmsConfig {
    @Value("${messagequeue.outbound.brokerURI}")
    private String brokerURI;
    @Value("${messagequeue.outbound.brokerUserName}")
    private String brokerUsername;
    @Value("${messagequeue.outbound.brokerPassword}")
    private String brokerPassword;

    @Value("${messagequeue.outbound.queueName}")
    private String queueName;

    @Bean(name = "connectionFactoryOutbound")
    public ConnectionFactory connectionFactoryOutbound() {
        System.out.println("==============brokerURI   ==" + brokerURI);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerURI);
        factory.setUserName(brokerUsername);
        factory.setPassword(brokerPassword);
        return factory;
    }

    @Bean(name = "jmsTemplateOutbound")
    public JmsTemplate jmsTemplateOutbound(@Qualifier("connectionFactoryOutbound") ConnectionFactory connectionFactoryOutbound) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryOutbound);
        jmsTemplate.setSessionTransacted(true);
        System.out.println("jmsTemplateOutbound=="+jmsTemplate);
        return jmsTemplate;
    }

    @Bean(name = "jmsListenerContainerFactoryOutbound")
    protected DefaultJmsListenerContainerFactory jmsListenerContainerFactoryOutbound(@Qualifier("connectionFactoryOutbound") ConnectionFactory connectionFactoryOutbound) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactoryOutbound);
        // factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> {
            // Add error handling logic here
        });
        return factory;
    }

}

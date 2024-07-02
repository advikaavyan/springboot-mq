package com.example.messagequeue.config;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableJms
@Slf4j
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
        log.info("==============brokerURI   ==" + brokerURI);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerURI);
        factory.setUserName(brokerUsername);
        factory.setPassword(brokerPassword);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(2);
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setBackOffMultiplier(2);
        redeliveryPolicy.setUseExponentialBackOff(true);

        factory.setRedeliveryPolicy(redeliveryPolicy);

        return factory;
    }

  /*  @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(2);
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setBackOffMultiplier(2);
        redeliveryPolicy.setUseExponentialBackOff(true);

        factory.setRedeliveryPolicy(redeliveryPolicy);
        return factory;
    }*/

    @Bean(name = "jmsTemplateInbound")
    @Qualifier("jmsTemplateInbound")
    @Primary
    public JmsTemplate jmsTemplateInbound(@Qualifier("connectionFactoryInbound") ConnectionFactory connectionFactoryInbound) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryInbound);
        jmsTemplate.setSessionTransacted(true);
        log.info("jmsTemplateInbound=="+jmsTemplate);
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

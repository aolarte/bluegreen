package com.andresolarte.bluegreen.service;

import com.andresolarte.bluegreen.model.Email;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Date;
import java.util.function.Consumer;

@Component
public class TestService {
    private final static Log LOG = LogFactory.getLog(TestService.class);

    private final JmsTemplate jmsTemplate;
    private final ZooKeeperService zooKeeperService;

    private final static String NODE_PATH ="/node";

    @Inject
    public TestService(JmsTemplate jmsTemplate, ZooKeeperService zooKeeperService) {
        this.jmsTemplate = jmsTemplate;
        this.zooKeeperService = zooKeeperService;
    }

    @PostConstruct
    public void start() {

        try {
            zooKeeperService.create(NODE_PATH, "empty".getBytes());
            zooKeeperService.getData(NODE_PATH, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    LOG.info("Got Data: " + new String(bytes));
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 15000)
    public void run() {
        Email email = new Email();
        email.setFrom("test@here.com");
        email.setSubject("Test Email");
        jmsTemplate.convertAndSend("mailbox", email, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("Version", "1");
                return message;
            }
        });

        jmsTemplate.convertAndSend("mailbox", email, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("Version", "2");
                return message;
            }
        });

        jmsTemplate.convertAndSend("mailbox", email);
        String s = new Date().toString();
        try {
            zooKeeperService.updateData(NODE_PATH, s.getBytes());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

package com.andresolarte.bluegreen.messaging;


import com.andresolarte.bluegreen.model.Email;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqV2Listener {


    @JmsListener(destination = "mailbox",
            selector = "(Version = '2') OR (Version IS NULL)")
    public void receiveMessage(Email email) {
        System.out.println("V2 Received <" + email + ">");
    }

}

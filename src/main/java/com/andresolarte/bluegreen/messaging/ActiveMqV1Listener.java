package com.andresolarte.bluegreen.messaging;


import com.andresolarte.bluegreen.model.Email;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqV1Listener {

    @JmsListener(destination = "mailbox", selector = "Version = '1'")
    public void receiveMessage(Email email) {
        System.out.println("V1 Received <" + email + ">");
    }


}

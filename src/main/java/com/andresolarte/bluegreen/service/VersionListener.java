package com.andresolarte.bluegreen.service;

import com.andresolarte.bluegreen.model.Versions;
import com.andresolarte.bluegreen.model.VersionsChangedEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class VersionListener {
    private final static Log LOG = LogFactory.getLog(VersionListener.class);

    public VersionListener() {
        LOG.info("Building");
    }

    @EventListener
    public void handleVersionsChangedEvent(VersionsChangedEvent event) {
        LOG.info("Versions event: " + event);
    }
}

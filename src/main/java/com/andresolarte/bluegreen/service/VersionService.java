package com.andresolarte.bluegreen.service;

import com.andresolarte.bluegreen.model.Versions;
import com.andresolarte.bluegreen.model.VersionsChangedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class VersionService {

    private final static Log LOG = LogFactory.getLog(VersionService.class);
    private static final String PATH = "/versions";

    private AtomicReference<Versions> versions = new AtomicReference<>(new Versions(0,100));

    @Autowired
    private ZooKeeperService zooKeeperService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher publisher;

    public Versions getVersions() {
        return versions.get();
    }


    public void updateVersion(Versions versions) {
        if (versions.getBlue() + versions.getGreen() != 100) {
            throw new RuntimeException("Must add up to 100");
        }
        try {
            zooKeeperService.updateData(PATH, serializeVersions(versions));
        } catch (Exception  e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serializeVersions(Versions versions) throws JsonProcessingException {
        return objectMapper.writeValueAsString(versions).getBytes();
    }

    @PostConstruct
    public void setup() {
        try {
            zooKeeperService.create(PATH, serializeVersions(versions.get()));
            zooKeeperService.getData(PATH, bytes -> readBytes(bytes));
        }   catch (Exception e) {
            LOG.error("Error registering listener: " + e.getMessage(), e);
        }
    }

    private void readBytes(byte[] bytes) {
        try {
            Versions data = objectMapper.readValue(bytes, Versions.class);
            LOG.info("Got data: " + data);
            versions.set(data);
            publisher.publishEvent(new VersionsChangedEvent(data));
            LOG.info("Done publishing changes");
        } catch (IOException e) {
            LOG.error("Error reading data: " + e.getMessage(), e);
        }

    }

}

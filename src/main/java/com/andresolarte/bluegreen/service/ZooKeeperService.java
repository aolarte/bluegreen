package com.andresolarte.bluegreen.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Component
public class ZooKeeperService {
    private final static Log LOG = LogFactory.getLog(ZooKeeperService.class);

    private ZooKeeper zoo;

    @Inject
    private ZooKeeperServerMain main;

    private class ReentrantWatcher implements Watcher {
        private final Consumer<byte[]> consumer;
        private final String path;
        private final CountDownLatch countDownLatch;

        public ReentrantWatcher(Consumer<byte[]> consumer, String path, CountDownLatch countDownLatch) {
            this.consumer = consumer;
            this.path = path;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            try {
                if (watchedEvent.getType() == Event.EventType.None) {
                    switch (watchedEvent.getState()) {
                        case Expired:
                            LOG.info("Watch Expired");
                            getDataAndAddWatch();
                            break;
                    }
                }
                if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                    LOG.info("Node Deleted");
                }
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {

                    byte[] bn = getDataAndAddWatch();
                    LOG.info("New Data");
                    consumer.accept(bn);

                } else {
                    LOG.info("Other event: " + watchedEvent.getType());

                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        private byte[] getDataAndAddWatch() throws KeeperException, InterruptedException {
            if (countDownLatch.getCount()>0) {
                return zoo.getData(path, this, null);
            } else {
                return zoo.getData(path, null, null); //No need to keep watching
            }

        }
    }

    public void create(String path, byte[] data) throws
            KeeperException, InterruptedException {
        zoo.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
    }

    public Stat updateData(String path, byte[] data) throws KeeperException, InterruptedException {
        return zoo.setData(path, data, -1); //match any version
    }

    public Stat nodeExists(String path) throws
            KeeperException, InterruptedException {

        return zoo.exists(path, true);
    }

    public void deleteNode(String path) throws KeeperException, InterruptedException {
        zoo.delete(path, -1); // Match any version
    }

    public CountDownLatch getData(String path, Consumer<byte[]> consumer) throws KeeperException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        byte[] b = zoo.getData(path, new ReentrantWatcher(consumer, path, countDownLatch), null);
        consumer.accept(b);
        return countDownLatch;
    }

    @PostConstruct
    // Method to connect zookeeper ensemble.
    public ZooKeeper connect() throws IOException, InterruptedException {
        String host = "localhost:2181";
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        zoo = new ZooKeeper(host, 50000, new Watcher() {

            public void process(WatchedEvent we) {

                if (we.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });

        connectedSignal.await();
        return zoo;
    }

    @PreDestroy
    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zoo.close();
    }
}

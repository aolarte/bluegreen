package com.andresolarte.bluegreen.config;

import com.andresolarte.bluegreen.zookeeper.ZooKeeperLocal;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZooKeeperConfig {

    @Bean
    ZooKeeperServerMain zooKeeper() {
        ZooKeeperLocal zookeeper = ZooKeeperLocal.builder().build();
       return  zookeeper.getZooKeeperServer();

    }
}

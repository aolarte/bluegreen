package com.andresolarte.bluegreen.service;

import com.andresolarte.bluegreen.model.Versions;
import com.andresolarte.bluegreen.model.VersionsChangedEvent;
import com.andresolarte.bluegreen.routing.DynamicWeightedLoadBalancer;
import org.apache.camel.processor.loadbalancer.DistributionRatio;
import org.apache.camel.processor.loadbalancer.WeightedLoadBalancer;
import org.apache.camel.processor.loadbalancer.WeightedRoundRobinLoadBalancer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@Component
public class VersionListener {
    private final static Log LOG = LogFactory.getLog(VersionListener.class);

    private final DynamicWeightedLoadBalancer loadBalancer;

    @Inject
    public VersionListener(DynamicWeightedLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;

    }

    @EventListener
    public void handleVersionsChangedEvent(VersionsChangedEvent event) {
        Versions versions = event.getVersions();
        List<Integer> ratios= Arrays.asList(versions.getBlue(), versions.getGreen());
        loadBalancer.updateRuntimeDistribution(ratios);
    }


}

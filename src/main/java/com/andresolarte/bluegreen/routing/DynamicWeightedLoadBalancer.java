package com.andresolarte.bluegreen.routing;

import org.apache.camel.processor.loadbalancer.DistributionRatio;
import org.apache.camel.processor.loadbalancer.WeightedRoundRobinLoadBalancer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicWeightedLoadBalancer extends WeightedRoundRobinLoadBalancer {
    public DynamicWeightedLoadBalancer(List<Integer> distributionRatios) {
        super(distributionRatios);
    }

    public void updateRuntimeDistribution(List<Integer> distributionRatios) {
        List<Integer> ratioList = getDistributionRatioList();
        if (ratioList.size() != distributionRatios.size()) {
            throw new RuntimeException("Invalid list size");
        }

        if (distributionRatiosAreDifferent(distributionRatios)) {


            ArrayList<DistributionRatio> runtimeRatios = new ArrayList<>();
            int position = 0;
            Iterator iterator = distributionRatios.iterator();

            while (iterator.hasNext()) {
                Integer value = (Integer) iterator.next();
                runtimeRatios.add(new DistributionRatio(position++, value.intValue()));
            }
            synchronized (this) {
                this.setDistributionRatioList(distributionRatios);
                this.setRuntimeRatios(runtimeRatios);
            }

        }
    }

    private boolean distributionRatiosAreDifferent(List<Integer> distributionRatios) {
        List<Integer> ratioList = getDistributionRatioList();
        for (int i = 0; i < ratioList.size(); i++) {
            if (ratioList.get(i) != distributionRatios.get(i)) {
                return true;
            }
        }
        return false;
    }
}

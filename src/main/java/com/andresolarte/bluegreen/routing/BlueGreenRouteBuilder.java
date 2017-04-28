package com.andresolarte.bluegreen.routing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.loadbalancer.WeightedLoadBalancer;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlueGreenRouteBuilder extends RouteBuilder {

    private final DynamicWeightedLoadBalancer loadBalancer;

    @Inject
    public BlueGreenRouteBuilder(DynamicWeightedLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }


    public static class MyLoadBalancer extends WeightedLoadBalancer {


        public MyLoadBalancer(List<Integer> distributionRatios) {
            super(distributionRatios);
        }

        @Override
        protected Processor chooseProcessor(List<Processor> list, Exchange exchange) {
            return null;
        }
    }

    @Override
    public void configure() throws Exception {

        ArrayList<Integer> distributionRatio = new ArrayList<>();
        distributionRatio.add(4);
        distributionRatio.add(2);
        distributionRatio.add(1);

        from("direct:startSelector").loadBalance(loadBalancer)
                .to("direct:blueSelector")
                .to("direct:greenSelector");


    }




}

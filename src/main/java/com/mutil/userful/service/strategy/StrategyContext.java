package com.mutil.userful.service.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StrategyContext {

    @Autowired
    private List<IStrategyService> strategyServiceList = new ArrayList<>();

    private Map<String, IStrategyService> strategyMap = new ConcurrentHashMap<>();

    public IStrategyService getStrategyService(String strategyName){
        if(strategyMap.isEmpty()){
            this.setStrategyMap();
        }
        return strategyMap.get(strategyName);
    }

    public void setStrategyMap(){
        strategyServiceList.stream().forEach(strategy->{
            strategyMap.put(strategy.getName(),strategy);
        });
    }

}

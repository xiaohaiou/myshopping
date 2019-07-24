package com.mutil.userful.service.strategy;

import org.springframework.stereotype.Service;

@Service
public class NormalStrategyService implements IStrategyService {

    @Override
    public String getName() {
        return StrategyEnum.STRATEGY_TYPE_1.getStrategyName();
    }

    @Override
    public Double getStrategyAccount(Double money) {
        return money;
    }
}

package com.mutil.userful.service.strategy;

import org.springframework.stereotype.Service;

@Service("svipStrategySevice")
public class SVipStrategySevice implements IStrategyService{

    @Override
    public String getName() {
        return StrategyEnum.STRATEGY_TYPE_3.getStrategyName();
    }

    /**
     * 超级会员 0.8折
     * @param money
     * @return
     */
    @Override
    public Double getStrategyAccount(Double money) {
        return 0.8*money;
    }
}

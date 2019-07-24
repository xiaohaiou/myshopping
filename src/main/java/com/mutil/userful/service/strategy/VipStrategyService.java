package com.mutil.userful.service.strategy;

import org.springframework.stereotype.Service;

@Service("vipStrategyService")
public class VipStrategyService implements IStrategyService {

    @Override
    public String getName() {
        return StrategyEnum.STRATEGY_TYPE_2.getStrategyName();
    }

    /**
     * vip 用户0.85折
     * @param money
     * @return
     */
    @Override
    public Double getStrategyAccount(Double money) {
        return 0.85*money;
    }
}

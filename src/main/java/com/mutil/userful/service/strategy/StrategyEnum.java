package com.mutil.userful.service.strategy;

public enum StrategyEnum {

    STRATEGY_TYPE_1(1,"normalStrategyService"),
    STRATEGY_TYPE_2(2,"vipStrategyService"),
    STRATEGY_TYPE_3(3,"svipStrategySevice")
    ;
    private Integer strategyId;
    private String strategyName;

    StrategyEnum(Integer strategyId,String strategyName){
        this.strategyId=strategyId;
        this.strategyName=strategyName;
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}

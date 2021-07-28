package com.mutil.userful.listener;

import com.mutil.userful.service.strategy.StrategyContext;
import com.mutil.userful.service.strategy.StrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestListener implements CommandLineRunner{

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Autowired
    private StrategyContext strategyContext;

    @Override
    public void run(String... args) throws Exception {
        strategyTest();
    }

    public void strategyTest(){
        Double result1 = strategyContext
                .getStrategyService(StrategyEnum.STRATEGY_TYPE_1.getStrategyName()).getStrategyAccount(101.0);
        Double result2 = strategyContext
                .getStrategyService(StrategyEnum.STRATEGY_TYPE_2.getStrategyName()).getStrategyAccount(100.0);
        Double result3 = strategyContext
                .getStrategyService(StrategyEnum.STRATEGY_TYPE_3.getStrategyName()).getStrategyAccount(100.0);
        logger.info("result1: "+result1+";"+
                "result2: "+result2+";"+
                "result3: "+result3+";");
    }

}

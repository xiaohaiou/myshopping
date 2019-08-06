package com.mutil.userful.service;

import com.mutil.userful.dao.UserfulMapper;
import com.mutil.userful.domain.Userful;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringTransationTest2Service {

    @Autowired
    private UserfulMapper userfulMapper;

//    @Transactional(rollbackFor = Exception.class)
    public void saveByTranstion2_1(){
        log.info("开始事务2_1..");
        Userful userful = new Userful();
        userful.setName("transtionName2_1");
        userful.setPhone("12222222222");
        userfulMapper.insert(userful);
        log.info("结束事务2_1..");
        ((SpringTransationTest2Service) AopContext.currentProxy()).saveByTranstion2_2();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,rollbackFor = Exception.class)
    public void saveByTranstion2_2(){
        log.info("开始事务2_2..");
        Userful userful = new Userful();
        userful.setName("transtionName2_2");
        userful.setPhone("12222222222");
        userfulMapper.insert(userful);


        userful = new Userful();
        userful.setName("transtionName2_2");
        userful.setPhone("122222222223");
        userfulMapper.insert(userful);
        log.info("结束事务2_2..");
    }

}

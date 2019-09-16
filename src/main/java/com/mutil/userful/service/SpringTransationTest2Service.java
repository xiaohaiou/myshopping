package com.mutil.userful.service;

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


//    @Transactional(rollbackFor = Exception.class)
    public void saveByTranstion2_1(){
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,rollbackFor = Exception.class)
    public void saveByTranstion2_2(){
    }

}

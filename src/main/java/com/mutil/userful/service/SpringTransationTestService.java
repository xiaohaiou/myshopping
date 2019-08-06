package com.mutil.userful.service;

import com.mutil.userful.dao.UserfulMapper;
import com.mutil.userful.domain.Userful;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *   1、Spring事务管理嵌套事务详解 : 同一个类中，一个方法调用另外一个有事务的方法 (事务不生效)
 *         解决方式：在java配置类上添加注解@EnableAspectJAutoProxy(exposeProxy = true)方式暴漏代理对象，
 *                   然后在service中通过代理对象AopContext.currentProxy()去调用方法。
 *
 *      PROPAGATION_REQUIRED -- 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。 
 *      PROPAGATION_SUPPORTS -- 支持当前事务，如果当前没有事务，就以非事务方式执行。 
 *      PROPAGATION_MANDATORY -- 支持当前事务，如果当前没有事务，就抛出异常。 
 *      PROPAGATION_REQUIRES_NEW -- 新建事务，如果当前存在事务，把当前事务挂起。 
 *      PROPAGATION_NOT_SUPPORTED -- 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。 
 *      PROPAGATION_NEVER -- 以非事务方式执行，如果当前存在事务，则抛出异常。 
 *      PROPAGATION_NESTED -- 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。
 */
@Slf4j
@Service
public class SpringTransationTestService {

    @Autowired
    private UserfulMapper userfulMapper;
    @Autowired
    private SpringTransationTest2Service springTransationTest2Service;

    @Transactional(rollbackFor = Exception.class)
    public void saveByTranstion1(){
        log.info("开始事务1..");
        Userful userful = new Userful();
        userful.setName("transtionName1_1");
        userful.setPhone("12222222222");
        userfulMapper.insert(userful);
        log.info("结束事务1..");
        springTransationTest2Service.saveByTranstion2_1();
    }

//    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
//    public void saveByTranstion2(){
//        log.info("开始事务2..");
//        Userful userful = new Userful();
//        userful.setName("transtionName1_2");
//        userful.setPhone("12222222222");
//        userfulMapper.insert(userful);
//        log.info("结束事务2..");
//        springTransationTest2Service.saveByTranstion2_1();
//    }

}

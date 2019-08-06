package com.mutil.userful.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Import({JDBC2MysqlConfiguration.class})
public class DataBaseMysqlConfiguration {

	private static Logger logger = LoggerFactory.getLogger(DataBaseMysqlConfiguration.class);
    @Autowired
    JDBC2MysqlConfiguration jdbc;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis:60000}")
    private Integer timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.minEvictableIdleTimeMillis:30000}")
    private Integer minEvictableIdleTimeMillis;
    @Value("${spring.datasource.testOnBorrow:false}")
    private Boolean testOnBorrow;
    @Value("${spring.datasource.testWhileIdle:true}")
    private Boolean testWhileIdle;
    @Value("${spring.datasource.testOnReturn:false}")
    private Boolean testOnReturn;
    @Value("${spring.datasource.poolPreparedStatements:true}")
    private Boolean poolPreparedStatements;
    @Value("${spring.datasource.maxOpenPreparedStatements:20}")
    private Integer maxOpenPreparedStatements;
    @Value("${spring.datasource.filters:stat}")
    private String filters;

	@Bean(name="dataSource", destroyMethod = "close", initMethod="init")  
    @Primary  
    public DataSource dataSource() {  
        logger.debug("Configruing Write druid DataSource");  
        logger.info("dataSource Configruing url="+jdbc.getUrl());
          
        DruidDataSource dataSource = new DruidDataSource();  
        dataSource.setUrl(jdbc.getUrl()); //allowMultiQueries=true
        dataSource.setUsername(jdbc.getUsername());//用户名  
        dataSource.setPassword(jdbc.getPassword());//密码  
        dataSource.setDriverClassName(driverClassName);  
        dataSource.setInitialSize(jdbc.getInitialSize());  
        dataSource.setMaxActive(jdbc.getMaxActive());  
        dataSource.setMinIdle(jdbc.getMinIdle());  
        dataSource.setMaxWait(jdbc.getMaxWait());  
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
        dataSource.setValidationQuery(jdbc.getValidationQuery());  
        dataSource.setTestOnBorrow(testOnBorrow);  
        dataSource.setTestWhileIdle(testWhileIdle);  
        dataSource.setTestOnReturn(testOnReturn);  
        dataSource.setPoolPreparedStatements(poolPreparedStatements);  
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxOpenPreparedStatements);  
        //配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙  
        try {
			dataSource.setFilters(filters);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
          
        return dataSource;  
    }
	
}

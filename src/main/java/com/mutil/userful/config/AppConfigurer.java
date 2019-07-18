package com.mutil.userful.config;

import com.mutil.userful.util.UserfulIntercepor;
import com.mutil.userful.util.XssFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfigurer implements WebMvcConfigurer {

    /**
     * 定义拦截器，用于判断session是否存在
     * @return
     */
    @Autowired
    private UserfulIntercepor intercepor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(intercepor)
                .addPathPatterns("/**")
                .excludePathPatterns("/validateLogin","/assets/**","/img/**","/css/**","/js/**","/font/**","/","/login");
    }

    /**
     * 注册过滤器XssFilter；用于防止XSS攻击
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", "/favicon.ico,/assets/*");
        initParameters.put("isIncludeRichText", "true");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}

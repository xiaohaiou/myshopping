package com.mutil.userful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.mutil.userful")
//@EnableEncryptableProperties //开启配置文件加密功能jasypt
public class UserfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserfulApplication.class, args);
	}

}


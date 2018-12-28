package com.mutil.userful;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEncryptableProperties //开启配置文件加密功能jasypt
public class UserfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserfulApplication.class, args);
	}

}


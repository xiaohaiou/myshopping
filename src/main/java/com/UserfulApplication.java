package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.mutil.userful")
@EnableMBeanExport(
		registration = RegistrationPolicy.IGNORE_EXISTING
)
public class UserfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserfulApplication.class, args);
	}

}


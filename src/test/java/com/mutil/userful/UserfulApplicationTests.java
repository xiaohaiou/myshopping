package com.mutil.userful;

import com.UserfulApplication;
import com.mutil.userful.service.SpringTransationTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes= UserfulApplication.class)
@AutoConfigureMockMvc
public class UserfulApplicationTests {

	@Autowired
	private SpringTransationTestService springTransationTestService;

	@Test
	public void contextLoads() {
		log.info("开始测试..");
		springTransationTestService.saveByTranstion1();
	}

}


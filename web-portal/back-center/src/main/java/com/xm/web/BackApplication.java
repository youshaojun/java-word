package com.xm.web;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yousj
 */
@EnableSpringUtil
@SpringBootApplication(scanBasePackages = "com.xm")
public class BackApplication {

	static {
		System.setProperty("spring.cloud.bootstrap.enabled", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

}

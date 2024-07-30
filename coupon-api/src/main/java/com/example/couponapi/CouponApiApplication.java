package com.example.couponapi;

import com.example.couponcore.CouponCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(CouponCoreConfiguration.class)
@SpringBootApplication
public class CouponApiApplication {

	public static void main(String[] args) {

		System.setProperty("spring.config.name", "application-api,application-core");
		SpringApplication.run(CouponApiApplication.class, args);
	}

}

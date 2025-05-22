package com.onion.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync//비동기(Async) 기능을 활성화하는 어노테이션 ✔ 멀티스레딩을 사용하여 백그라운드에서 작업 실행 가능
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

package org.chiu.micro.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(proxyBeanMethods = false)
public class MicroWebSocketApplication {

	public static void main(String[] args) {
				SpringApplication.run(MicroWebSocketApplication.class, args);
	}

}

package com.hdkj.notify;

import java.io.IOException;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.hdkj.notify.advance.Server;

@SpringBootApplication
public class Application {
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
    public ActiveMQQueue queue() {
       return new ActiveMQQueue("sample.queue");
    }
}

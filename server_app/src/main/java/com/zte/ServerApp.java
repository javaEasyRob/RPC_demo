package com.zte;

import com.zte.sever.RpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.zte")
public class ServerApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerApp.class);
        context.start();
    }

    @Bean
    public RpcServer getRpcServer() {
        return new RpcServer();
    }
}

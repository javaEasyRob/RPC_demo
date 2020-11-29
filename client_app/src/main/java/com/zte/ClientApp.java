package com.zte;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 */
@Configuration
@ComponentScan(basePackages = "com.zte")
public class ClientApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientApp.class);
        context.start();
        final ServiceTest helloService = context.getBean(ServiceTest.class);
        System.out.println(helloService.hello("rpc我也会啦！！"));
    }
}

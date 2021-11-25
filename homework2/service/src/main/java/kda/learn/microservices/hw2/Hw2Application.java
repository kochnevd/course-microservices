package kda.learn.microservices.hw2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(value = {"classpath:applicationContext.xml"})
public class Hw2Application {

    public static void main(String[] args) {
        SpringApplication.run(Hw2Application.class, args);
    }

}

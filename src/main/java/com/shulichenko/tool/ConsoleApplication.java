package com.shulichenko.tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsoleApplication {

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ConsoleApplication.class, args)));
    }
}

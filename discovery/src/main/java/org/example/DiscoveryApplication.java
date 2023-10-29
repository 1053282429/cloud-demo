package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 10532
 */
@SpringBootApplication
public class DiscoveryApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(DiscoveryApplication.class);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
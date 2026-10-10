package com.smxplore.proto;

import org.springframework.boot.SpringApplication;

public class TestAhorasiApplication {

    public static void main(String[] args) {
        SpringApplication.from(AhorasiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

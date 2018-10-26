package com.backbase.billpay.fiserv;

import com.backbase.buildingblocks.backend.configuration.autoconfigure.BackbaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@BackbaseApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

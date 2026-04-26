package com.example.smart_university_devices_and_materials_maintanance_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartUniversityDevicesAndMaterialsMaintananceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartUniversityDevicesAndMaterialsMaintananceSystemApplication.class, args);
    }

}

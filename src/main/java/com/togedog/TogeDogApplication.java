package com.togedog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TogeDogApplication {
  static {
    System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
  }
  public static void main(String[] args) {
    SpringApplication.run(TogeDogApplication.class, args);
  }

  //
}

package com.sprint.example.sb01part2hrbankteam10ref;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Sb01Part2HrbankTeam10Application {

  public static void main(String[] args) {
    SpringApplication.run(Sb01Part2HrbankTeam10Application.class, args);
  }
}

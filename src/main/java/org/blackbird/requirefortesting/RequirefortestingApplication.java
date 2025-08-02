package org.blackbird.requirefortesting;

import org.blackbird.requirefortesting.requirements.service.RequirementService;
import org.blackbird.requirefortesting.security.internal.PostgresUserDetailsService;
import org.blackbird.requirefortesting.security.model.CreateUserDto;
import org.blackbird.requirefortesting.shared.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RequirefortestingApplication {

  public static void main(String[] args) {
    SpringApplication.run(RequirefortestingApplication.class, args);
  }

  @Bean
  CommandLineRunner init(
      PostgresUserDetailsService postgresUserDetailsService,
      RequirementService requirementService) {
    return args -> {
      CreateUserDto max =
          new CreateUserDto("Max", "password", "max@gmail.com", Role.REQUIREMENTS_ENGINEER);
      CreateUserDto john = new CreateUserDto("John", "password", "john@gmail.com", Role.TESTER);
      CreateUserDto robert =
          new CreateUserDto("Robert", "password", "robert@gmail.com", Role.TEST_MANAGER);
      CreateUserDto luca =
          new CreateUserDto("Luca", "password", "luca@gmail.com", Role.TEST_CASE_CREATOR);

      postgresUserDetailsService.registerUser(max);
      postgresUserDetailsService.registerUser(john);
      postgresUserDetailsService.registerUser(robert);
      postgresUserDetailsService.registerUser(luca);

      //      CreateOrUpdateRequirementDto requirement1 =
      //          new CreateOrUpdateRequirementDto(
      //              "Requirement 1", "Description for requirement 1", Priority.MEDIUM,
      // Status.OPEN);
      //      CreateOrUpdateRequirementDto requirement2 =
      //          new CreateOrUpdateRequirementDto(
      //              "Requirement 2", "Description for requirement 2", Priority.HIGH,
      // Status.IN_PROGRESS);
      //      CreateOrUpdateRequirementDto requirement3 =
      //          new CreateOrUpdateRequirementDto(
      //              "Requirement 3", "Description for requirement 3", Priority.LOW, Status.OPEN);
      //
      //      Long userIdMax = 1L;
      //      Long userIdJohn = 2L;
      //      Long userIdRobert = 3L;
      //
      //      requirementService.createRequirement(requirement1, userIdMax);
      //      requirementService.createRequirement(requirement2, userIdJohn);
      //      requirementService.createRequirement(requirement3, userIdRobert);
    };
  }
}

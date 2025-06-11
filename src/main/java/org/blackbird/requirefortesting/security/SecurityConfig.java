package org.blackbird.requirefortesting.security;

import org.blackbird.requirefortesting.shared.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
class SecurityConfig {

  @Bean
  UserDetailsService userDetailsService() {
    UserDetails reqEngineer =
        User.builder()
            .username("engineer")
            .password("{noop}password")
            .roles(Role.REQUIREMENTS_ENGINEER.getRoleName())
            .build();

    UserDetails testManager =
        User.builder()
            .username("testmanager")
            .password("{noop}password")
            .roles(Role.TEST_MANAGER.getRoleName())
            .build();

    UserDetails testCreator =
        User.builder()
            .username("testcreator")
            .password("{noop}password")
            .roles(Role.TEST_CASE_CREATOR.getRoleName())
            .build();

    UserDetails tester =
        User.builder()
            .username("tester")
            .password("{noop}password")
            .roles(Role.TESTER.getRoleName())
            .build();

    return new InMemoryUserDetailsManager(reqEngineer, testManager, testCreator, tester);
  }
}

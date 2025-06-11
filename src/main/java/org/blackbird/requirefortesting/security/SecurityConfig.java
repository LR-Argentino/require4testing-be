package org.blackbird.requirefortesting.security;

import org.blackbird.requirefortesting.shared.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

    return http.build();
  }

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

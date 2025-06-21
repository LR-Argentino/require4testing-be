// package org.blackbird.requirefortesting.security;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.test.context.ContextConfiguration;
//
// @WebMvcTest
// @ContextConfiguration(classes = SecurityConfig.class)
// class SecurityConfigTests {
//  @Autowired private UserDetailsService userDetailsService;
//
//  @Test
//  void testUserDetailsServiceContainsRequirementEngineer() {
//    testUserDetailsServiceContains("engineer", "ROLE_REQUIREMENTS_ENGINEER");
//    testUserDetailsServiceContains("testmanager", "ROLE_TEST_MANAGER");
//    testUserDetailsServiceContains("testcreator", "ROLE_TEST_CASE_CREATOR");
//    testUserDetailsServiceContains("tester", "ROLE_TESTER");
//  }
//
//  private void testUserDetailsServiceContains(String username, String role) {
//    var user = userDetailsService.loadUserByUsername(username);
//    assertThat(user).isNotNull();
//    assertThat(user.getUsername()).isEqualTo(username);
//    assertThat(user.getAuthorities()).extracting("authority").contains(role);
//  }
// }

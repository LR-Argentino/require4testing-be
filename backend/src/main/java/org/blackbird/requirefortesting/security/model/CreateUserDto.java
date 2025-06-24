package org.blackbird.requirefortesting.security.model;

import org.blackbird.requirefortesting.shared.Role;

public record CreateUserDto(String username, String password, String email, Role role) {}

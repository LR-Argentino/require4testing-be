package org.blackbird.requirefortesting.security.model;

import java.util.List;

public record UserDto(Long id, String username, List<String> authorities, String email) {}

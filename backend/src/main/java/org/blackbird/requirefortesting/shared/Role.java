package org.blackbird.requirefortesting.shared;

public enum Role {
  REQUIREMENTS_ENGINEER("REQUIREMENTS_ENGINEER"),
  TEST_CASE_CREATOR("TEST_CASE_CREATOR"),
  TESTER("TESTER"),
  TEST_MANAGER("TEST_MANAGER");

  private final String roleName;

  Role(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }
}

package org.blackbird.requirefortesting.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
class SharedModuleTests {

  @Test
  void shouldHaveAllStatusValues() {
    // Given & When
    Status[] statuses = Status.values();

    // Then
    assertThat(statuses).hasSize(3);
    assertThat(statuses).containsExactlyInAnyOrder(Status.OPEN, Status.IN_PROGRESS, Status.CLOSED);
  }

  @Test
  void shouldHaveAllPriorityValues() {
    // Given & When
    Priority[] priorities = Priority.values();

    // Then
    assertThat(priorities).hasSize(3);
    assertThat(priorities).containsExactlyInAnyOrder(Priority.LOW, Priority.MEDIUM, Priority.HIGH);
  }
}

package org.blackbird.requirefortesting.testmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_run")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRun {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  private String description;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private TestRunStatus status = TestRunStatus.PLANNED;

  @Column(nullable = false)
  private Long createdBy;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "test_run_test_case",
      joinColumns = @JoinColumn(name = "test_run_id"),
      inverseJoinColumns = @JoinColumn(name = "test_case_id"))
  @Builder.Default
  private Set<TestCase> testCases = new HashSet<>();
}

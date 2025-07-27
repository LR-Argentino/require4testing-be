package org.blackbird.requirefortesting.testmanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "test_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
  private TestRunStatus status = TestRunStatus.PLANNED;

  @Column(nullable = false)
  private Long createdBy;

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "test_run_test_case",
      joinColumns = @JoinColumn(name = "test_run_id"),
      inverseJoinColumns = @JoinColumn(name = "test_case_id"))
  @Builder.Default
  @JsonManagedReference
  private Set<TestCase> testCases = new HashSet<>();
}

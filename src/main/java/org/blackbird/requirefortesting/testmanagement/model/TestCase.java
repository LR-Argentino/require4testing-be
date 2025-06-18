package org.blackbird.requirefortesting.testmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;

@Table(name = "test_case")
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "requirement_id", nullable = false)
  private Long reuqirementId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Status status = Status.OPEN;

  @Enumerated(EnumType.STRING)
  private TestResult testResult;

  @Column(nullable = false)
  private Long createdBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "creation_date", nullable = false)
  private LocalDateTime creationDate;

  @ManyToMany(mappedBy = "testCases", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<TestRun> testRuns = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    updatedAt = LocalDateTime.now();
    creationDate = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}

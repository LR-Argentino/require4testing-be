package org.blackbird.requirefortesting.testmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.blackbird.requirefortesting.shared.Status;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "test_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TestCase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "requirement_id", nullable = false)
  private Long requirementId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private Status status = Status.OPEN;

  @Enumerated(EnumType.STRING)
  private TestResult testResult;

  @Column(nullable = false)
  private Long createdBy;

  @Column(name = "updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "creation_date", nullable = false)
  @CreatedDate
  private LocalDateTime creationDate;

  @ManyToMany(mappedBy = "testCases", fetch = FetchType.LAZY)
  @Builder.Default
  @JsonBackReference
  private Set<TestRun> testRuns = new HashSet<>();
}

package org.blackbird.requirefortesting.testexecution.model;

import jakarta.persistence.*;
import lombok.*;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestResult;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;

@Entity
@Table(name = "test_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "test_run_id")
  private TestRun testRun;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "test_case_id")
  private TestCase testCase;

  @Column(name = "tester_id", nullable = false)
  private Long testerId;

  @Enumerated(EnumType.STRING)
  private TestResult testResult;

  @Column(columnDefinition = "TEXT")
  private String comment;
}

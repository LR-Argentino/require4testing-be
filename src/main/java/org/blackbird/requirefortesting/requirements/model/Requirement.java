package org.blackbird.requirefortesting.requirements.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "requirement")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Requirement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = true, length = 500)
  private String description;

  @Column(nullable = false)
  private Priority priority;

  @Column(nullable = false)
  @Builder.Default
  private Status status = Status.OPEN;

  @LastModifiedDate private LocalDateTime updatedAt;

  @CreatedDate private LocalDateTime createdAt;
}

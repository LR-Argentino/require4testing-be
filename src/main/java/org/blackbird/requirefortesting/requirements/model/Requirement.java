package org.blackbird.requirefortesting.requirements.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;

@Table(name = "requirement")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Requirement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  private String description;

  @Column(nullable = false)
  private Priority priority;

  @Column(nullable = false)
  private Status status = Status.OPEN;

  private LocalDateTime updatedAt;

  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    updatedAt = LocalDateTime.now();
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}

package org.blackbird.requirefortesting.testmanagement.internal.repository;

import java.util.Optional;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
  @Query("SELECT t FROM TestCase t LEFT JOIN FETCH t.testRuns WHERE t.id = :id")
  Optional<TestCase> findByIdWithTestRuns(@Param("id") Long id);
  
  boolean existsByRequirementId(Long requirementId);
}

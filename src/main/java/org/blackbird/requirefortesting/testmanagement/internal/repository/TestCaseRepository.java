package org.blackbird.requirefortesting.testmanagement.internal.repository;

import java.util.List;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
  @Query("SELECT t FROM TestCase t WHERE t.requirementId = :requirementId")
  List<TestCase> findTestCasesByRequirementId(@Param("requirementId") Long requirementId);
}

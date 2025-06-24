package org.blackbird.requirefortesting.testmanagement.internal.repository;

import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {}

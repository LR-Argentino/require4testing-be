package org.blackbird.requirefortesting.testmanagement.internal.repository;

import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestManagementRepository extends JpaRepository<TestCase, Long> {}

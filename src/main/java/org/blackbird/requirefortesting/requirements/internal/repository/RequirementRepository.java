package org.blackbird.requirefortesting.requirements.internal.repository;

import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {}

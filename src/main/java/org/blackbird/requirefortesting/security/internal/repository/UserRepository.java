package org.blackbird.requirefortesting.security.internal.repository;

import java.util.Optional;
import org.blackbird.requirefortesting.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByUsername(String username);

  Optional<User> findUserByEmail(String email);
}

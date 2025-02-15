package com.github.asyu.restapiwithboot.accounts;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String username);
}

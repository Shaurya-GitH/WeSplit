package com.wesplit.main.repositories;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance,Long> {
    Optional<Balance> findByUser1AndUser2(User user1, User user2);
}

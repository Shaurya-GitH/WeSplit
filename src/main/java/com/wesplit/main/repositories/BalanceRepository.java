package com.wesplit.main.repositories;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Balance> findByUser1AndUser2(User user1, User user2);
}

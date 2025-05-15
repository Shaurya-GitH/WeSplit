package com.wesplit.main.repositories;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("from Balance b where b.user1=:user1 and b.user2=:user2 and b.groupId is null")
    Optional<Balance> findByUser1AndUser2(@Param("user1") User user1,@Param("user2") User user2);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("from Balance b where b.user1=?1 and b.user2=?2 and b.groupId=?3")
    Optional<Balance> findByUser1AndUser2Group(User user1,User user2,Long groupId);
    List<Balance> findByGroupId(Long groupId);

    List<Balance> findByGroupIdAndUser1OrUser2AndGroupId(Long groupId, User user1, User user2, Long groupId1);
}

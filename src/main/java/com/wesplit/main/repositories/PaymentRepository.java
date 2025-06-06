package com.wesplit.main.repositories;

import com.wesplit.main.entities.Payment;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByPaidByAndPaidToAndGroupIdOrPaidByAndPaidToAndGroupId(User paidBy, User paidTo,Long groupId, User paidBy1, User paidTo1,Long groupId1);

    List<Payment> findByGroupId(Long groupId);
}

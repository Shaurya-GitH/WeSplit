package com.wesplit.main.repositories;

import com.wesplit.main.entities.Payment;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByPaidByAndPaidToOrPaidByAndPaidTo(User paidBy, User paidTo, User paidBy2, User paidTo2);

    List<Payment> findByGroupId(Long groupId);
}

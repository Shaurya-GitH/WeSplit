package com.wesplit.main.repositories;

import com.wesplit.main.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    List<Expense> findByGroupId(Long groupId);

    List<Expense> findByGroupIdAndSettled(Long groupId, Boolean settled);
}

package com.wesplit.main.repositories;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,Long> {
    @Query("select distinct e1.expense from ExpenseSplit e1 where e1.expense.settled=:settled and e1.user=:user1 and e1.expense.groupId is null and e1.expense  in (select e2.expense from ExpenseSplit e2 where e2.user=:user2 and e2.expense.groupId is null)")
    List<Expense> getExpenses(User user1, User user2,boolean settled);
    List<ExpenseSplit> findByExpenseExpenseId(Long expenseId);
}

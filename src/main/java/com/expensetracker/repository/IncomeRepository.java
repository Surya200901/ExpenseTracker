package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user")
    double getTotalIncome(@Param("user") User user);
    
    List<Income> findAllByUser(User user);
    
    @Query("SELECT i FROM Income i WHERE i.user = :user ORDER BY i.date DESC")
    List<Income> findTop5ByUserOrderByDateDesc(@Param("user") User user);
}

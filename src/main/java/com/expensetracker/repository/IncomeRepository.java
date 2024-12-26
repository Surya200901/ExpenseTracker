package com.expensetracker.repository;

import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT SUM(i.amount) FROM Income i")
    double getTotalIncome();

    List<Income> findTop5ByOrderByDateDesc();

    // Add this method to find all incomes by user
    List<Income> findAllByUser(User user);
}

package com.cursotdd.libraryapi.model.repository;

import com.cursotdd.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}

package com.cursotdd.libraryapi.service.impl;

import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.model.repository.LoanRepository;
import com.cursotdd.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}

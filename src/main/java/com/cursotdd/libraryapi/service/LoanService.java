package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.dto.LoanFilterDTO;
import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.resource.controller.BookController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
}

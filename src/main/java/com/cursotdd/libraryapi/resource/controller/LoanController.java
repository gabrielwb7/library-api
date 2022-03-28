package com.cursotdd.libraryapi.resource.controller;

import com.cursotdd.libraryapi.dto.LoanDTO;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.service.BookService;
import com.cursotdd.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return loan.getId();
    }

















}

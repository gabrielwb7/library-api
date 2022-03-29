package com.cursotdd.libraryapi.model.repository;

import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.cursotdd.libraryapi.model.repository.BookRepositoryTest.getBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;

    @Test
    public void existsByBookAndNotReturned() {
        Loan loan = createLoan();

        boolean exists = repository.existsByBookAndNotReturned(loan.getBook());

        assertThat(exists).isTrue();
    }

    @Test
    public void findByBookIsbnOrCustomerTest() {
        Loan loan = createLoan();

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0,10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Collections.singletonList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    public void notFindByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createLoan();

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }

    @Test
    public void findByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createLoan();
        loan.setLoanDate(LocalDate.now().minusDays(5));

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    private Loan createLoan() {
        Book book = getBook();
        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();

        entityManager.persist(book);
        entityManager.persist(loan);

        return loan;
    }


















}

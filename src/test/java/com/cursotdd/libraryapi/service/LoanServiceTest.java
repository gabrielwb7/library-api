package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.model.repository.LoanRepository;
import com.cursotdd.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    public void saveLoanTest() {
        Book book = createValidBook();
        book.setId(1l);

        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().id(1l).book(book).loanDate(LocalDate.now()).customer("Fulano").build();

        when(repository.save(loan)).thenReturn(savedLoan);

        Loan savingLoan = service.save(loan);

        assertThat(savingLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(savingLoan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(savingLoan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }



    private Book createValidBook() {
        return Book.builder().author("J.R.R Tolkien").title("O senhor dos anéis").isbn("001").build();
    }
}

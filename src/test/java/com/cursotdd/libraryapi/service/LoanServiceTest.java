package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursotdd.libraryapi.api.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.model.repository.LoanRepository;
import com.cursotdd.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);

        Loan savingLoan = service.save(loan);

        assertThat(savingLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(savingLoan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(savingLoan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    public void loanedBookSaveTest() {
        Loan loan = createLoan();

        when(repository.existsByBookAndNotReturned(loan.getBook())).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(loan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(loan);
    }

    @Test
    public void getLoanDetailsTest() {
        Loan loan = createLoan();
        loan.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(1L);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(1L);
    }

    @Test
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updateLoan = service.update(loan);

        assertThat(updateLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    @Test
    public void findLoanTest() {

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Loan> page = new PageImpl<Loan>(Arrays.asList(loan), pageRequest, 1);

        when(repository.findByBookIsbnOrCustomer(
                anyString(),anyString(), any(PageRequest.class))).thenReturn(page);

        LoanFilterDTO dto = LoanFilterDTO.builder().customer(loan.getCustomer()).isbn(loan.getBook().getIsbn()).build();

        Page<Loan> result =service.find(dto, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }


    public static Loan createLoan() {
        Book book = createValidBook();
        book.setId(1l);

        return Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

    private static Book createValidBook() {
        return Book.builder().author("J.R.R Tolkien").title("O senhor dos anéis").isbn("001").build();
    }
























}

package com.cursotdd.libraryapi.resource.controller;

import com.cursotdd.libraryapi.api.dto.LoanDTO;
import com.cursotdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursotdd.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursotdd.libraryapi.api.controller.LoanController;
import com.cursotdd.libraryapi.api.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.entity.Loan;
import com.cursotdd.libraryapi.service.BookService;
import com.cursotdd.libraryapi.service.EmailService;
import com.cursotdd.libraryapi.service.LoanService;
import com.cursotdd.libraryapi.service.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @MockBean
    private EmailService emailService;

    @Test
    public void createLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").email("customeremail@email.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    public void invalidISBNCreateLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").email("fulano@email.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").email("fulano@email.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    public void returnBookTest() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    public void returnInexistentBookTest() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void findLoanTest() throws Exception {
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(1L);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1) );

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }


















}

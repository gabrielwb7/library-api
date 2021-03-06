package com.cursotdd.libraryapi.resource.controller;

import com.cursotdd.libraryapi.api.dto.BookDTO;
import com.cursotdd.libraryapi.api.controller.BookController;
import com.cursotdd.libraryapi.api.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.service.EmailService;
import com.cursotdd.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import com.cursotdd.libraryapi.service.BookService;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    private static final String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService service;

    @MockBean
    LoanService loanService;

    @MockBean
    EmailService emailService;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTestWithSucess() throws Exception {

        BookDTO bookDTO = BookDTO.builder().author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build();
        Book savedBook = Book.builder().id(10L).author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String data = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(data);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Deve lan??ar uma exce????o quando os dados n??o informados n??o for suficiente para cria????o")
    public void createInvalidBookTest() throws Exception {
        String data = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(data);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Deve lan??ar erro ao tentar cadastrar um livro com um ISBN j?? utilizado")
    public void createBookWithDuplicateIsbn() throws Exception {
        BookDTO bookDTO = BookDTO.builder().author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build();
        String data = new ObjectMapper().writeValueAsString(bookDTO);
        String errorMessage = "ISBN j?? est?? cadastrado";

        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(data);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));

    }

    @Test
    public void getBookDetailsTest() throws Exception {
        Long id = 1l;

        Book book = Book.builder()
                        .id(id)
                        .title(createNewBook().getTitle())
                        .author(createNewBook().getAuthor())
                        .isbn(createNewBook().getIsbn())
                        .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

       mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(jsonPath("id").value(id))
               .andExpect(jsonPath("title").value(createNewBook().getTitle()))
               .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
               .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    public void bookNotFoundTest() throws Exception{
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookTest() throws Exception{
        Long id = 1l;

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book george_orwell = Book.builder().id(1l).title("1984").author("George Orwell").isbn("321").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(george_orwell));
        BDDMockito.given(service.update(george_orwell)).willReturn(Book.builder().id(1L).author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));;
    }

    @Test
    public void updateBookButNotFoundTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteBookTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteBookButNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void findBooksTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder().author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1) );

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

       mockMvc.perform(request)
               .andExpect(status().isOk())
               .andExpect(jsonPath("content", hasSize(1)))
               .andExpect(jsonPath("totalElements").value(1))
               .andExpect(jsonPath("pageable.pageSize").value(100))
               .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("J.R.R Tolkien").title("O senhor dos an??is").isbn("001").build();
    }

}
























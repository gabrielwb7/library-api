package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.cursotdd.libraryapi.service.impl.BookServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    @MockBean
    private BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl( repository );
    }

    @Test
    @DisplayName("Deve salvar um livro com sucesso")
    public void saveBookTest() {
        Book book = createValidBook(Book.builder());

        when(service.save(book)).thenReturn(createValidBook(Book.builder().id(1L)));
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void shouldNotSaveABookWithDuplicatedISBN() {
        Book book = createValidBook(Book.builder());

        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> service.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já está cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }


    private Book createValidBook(Book.BookBuilder builder) {
        return builder.author("J.R.R Tolkien").title("O senhor dos anéis").isbn("001").build();
    }
















}

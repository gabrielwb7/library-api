package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.cursotdd.libraryapi.service.impl.BookServiceImpl;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        Book book = createValidBook();

        when(service.save(book)).thenReturn(Book.builder().id(1l)
                .isbn("001")
                .author("J.R.R Tolkien")
                .title("O senhor dos anéis").build());
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void shouldNotSaveABookWithDuplicatedISBN() {
        Book book = createValidBook();

        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> service.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já está cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    public void getByIdTest() {
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
    }

    @Test
    public void bookNotFoundByIdTest() {
        Long id = 1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    public void updateBookTest() {
        Book book = Book.builder().id(1l).build();

        Book updateBook = createValidBook();
        updateBook.setId(1l);

        when(repository.save(book)).thenReturn(updateBook);

        Book newBook = service.update(book);

        assertDoesNotThrow(() -> service.update(book));

        assertThat(newBook.getId()).isEqualTo(updateBook.getId());
        assertThat(newBook.getIsbn()).isEqualTo(updateBook.getIsbn());
        assertThat(newBook.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(newBook.getAuthor()).isEqualTo(updateBook.getAuthor());
    }

    @Test
    public void updateInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    public void deleteBookTest() {
        Book book = Book.builder().id(1l).build();

        assertDoesNotThrow(() -> service.deleteBook(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    public void deleteInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.deleteBook(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    public void findBookTest() {

        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), pageRequest, 1);

        when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Book> result =service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(book));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    public void getBookByIsbn() {
        String isbn = "1230";

        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }




    private Book createValidBook() {
        return Book.builder().author("J.R.R Tolkien").title("O senhor dos anéis").isbn("001").build();
    }
















}

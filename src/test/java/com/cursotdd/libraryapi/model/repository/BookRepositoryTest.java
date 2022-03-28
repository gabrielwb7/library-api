package com.cursotdd.libraryapi.model.repository;

import com.cursotdd.libraryapi.model.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.atomicReference;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    public void returnTrueWhenISBNExists() {
        String isbn = "123";
        Book book = getBook();

        entityManager.persist(book);
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    public void returnFalseWhenISBNDoesntExists() {
        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    public void findByIdTest() {
        Long id = 1l;
        Book book = getBook();

        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    public void saveBookTest() {
        Book book = getBook();

        Book savedBook = repository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    protected static Book getBook() {
        return Book.builder().author("J.R.R Tolkien").title("O senhor dos anéis").isbn("123").build();
    }

    @Test
    public void deleteBookTest() {
        Book book = getBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        repository.delete(book);

        Book deleteBook = entityManager.find(Book.class, book.getId());

        assertThat(deleteBook).isNull();
    }

}

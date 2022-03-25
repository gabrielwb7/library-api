package com.cursotdd.libraryapi.service;

import com.cursotdd.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.DoubleStream;

public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);

    void deleteBook(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);
}

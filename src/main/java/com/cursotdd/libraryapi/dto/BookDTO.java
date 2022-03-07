package com.cursotdd.libraryapi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;


}

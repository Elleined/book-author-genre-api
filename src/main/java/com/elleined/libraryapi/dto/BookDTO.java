package com.elleined.libraryapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class BookDTO {
    private int id;

    @NotBlank(message = "Book title cannot be null or empty")
    private String title;

    @NotBlank(message = "Book isbn cannot be null or empty")
    private String isbn;

    @NotBlank(message = "Book description cannot be null or empty")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Book published date cannot be null or empty")
    private LocalDate publishedDate;

    @Min(value = 10, message = "Book pages cannot be less than 10")
    private int pages;

    private int views;

    private int authorId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Integer> genreIds;
}

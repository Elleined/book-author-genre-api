package com.denielle.api.restapi.service;

import com.denielle.api.restapi.dto.BookDTO;
import com.denielle.api.restapi.exception.NotFoundException;
import com.denielle.api.restapi.model.Author;
import com.denielle.api.restapi.model.Book;
import com.denielle.api.restapi.model.Genre;
import com.denielle.api.restapi.repository.AuthorRepository;
import com.denielle.api.restapi.repository.BookRepository;
import com.denielle.api.restapi.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final Random random = new Random();

    public BookDTO getById(int id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id of " + id + " does not exists"));
        book.setViews(book.getViews() + 1);

        return this.convertToDTO(book);
    }

    public BookDTO getByTitle(String title) {
        Book book = bookRepository.fetchByTitle(title).orElseThrow(() -> new NotFoundException("Book with title of " + title + " does not exits"));
        book.setViews(book.getViews() + 1);

        return this.convertToDTO(book);
    }

    public BookDTO getByIsbn(String isbn) {
        Book book = bookRepository.fetchByIsbn(isbn).orElseThrow(() -> new NotFoundException("Book with isbn of " + isbn + " does not exits"));
        book.setViews(book.getViews() + 1);

        return this.convertToDTO(book);
    }

    public List<BookDTO> getAllById(List<Integer> bookIds) {
        return bookIds.stream()
                .map(this::getById)
                .toList();
    }

    public List<BookDTO> getAllByGenre(String genreName) {
        List<BookDTO> books = bookRepository.getAllByGenre(genreName)
                .stream()
                .map(this::convertToDTO)
                .toList();

        books.forEach(book -> book.setViews(book.getViews() + 1));

        return books;
    }

    public List<BookDTO> getAllByTitleFirstLetter(char firstLetter) {
        List<BookDTO> books = bookRepository.getAllByTitleFirstLetter(firstLetter)
                .stream()
                .map(this::convertToDTO)
                .toList();

        books.forEach(book -> book.setViews(book.getViews() + 1));

        return books;
    }

    public List<BookDTO> getAll() {
        List<BookDTO> books = bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

        books.forEach(book -> book.setViews(book.getViews() + 1));

        return books;
    }

    public List<BookDTO> getAll(int pageNumber, int pageSize) {
        Pageable pageable = PageSorter.getPage(pageNumber, pageSize);

        List<BookDTO> books = bookRepository.findAll(pageable)
                .stream()
                .map(this::convertToDTO)
                .toList();

        books.forEach(book -> book.setViews(book.getViews() + 1));

        return books;
    }

    public List<BookDTO> getAll(int pageNumber, int pageSize, String sortDirection, String sortProperty) {
        Pageable pageable = PageSorter.getPage(pageNumber, pageSize, sortDirection, sortProperty);

        List<BookDTO> books = bookRepository.findAll(pageable)
                .stream()
                .map(this::convertToDTO)
                .toList();

        books.forEach(book -> book.setViews(book.getViews() + 1));

        return books;
    }

    public List<Integer> saveAll(List<BookDTO> books) {
        return books.stream()
                .map(this::save)
                .toList();
    }

    public int save(BookDTO bookDTO) {
        Author author = authorRepository.fetchByName(bookDTO.getAuthorName()).orElseThrow(() -> new NotFoundException("Author with name of " + bookDTO.getAuthorName() + " does not exists"));
        author.setBookCount(author.getBookCount() + 1);
        authorRepository.save(author);

        Set<Genre> genres = bookDTO.getGenres()
                .stream()
                .map(name -> genreRepository.fetchByName(name)
                        .orElseThrow(() -> new NotFoundException("Genre with name of " + name + " does not exists")))
                .collect(Collectors.toSet());

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .isbn(bookDTO.getIsbn())
                .pages(bookDTO.getPages())
                .publishedDate(bookDTO.getPublishedDate())
                .createdAt(LocalDateTime.now())
                .author(author)
                .genres(genres)
                .saleCount(random.nextInt(999))
                .build();

        bookRepository.save(book);
        log.debug("Book saved successfully {}", book.getTitle());
        return book.getId();
    }

    public void update(int id, BookDTO bookDTO) {
        Author author = authorRepository.fetchByName(bookDTO.getAuthorName()).orElseThrow(() -> new NotFoundException("Author with name of " + bookDTO.getAuthorName() + " does not exists"));
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id of " + id + " does not exists"));

        Set<Genre> genres = bookDTO.getGenres()
                .stream()
                .map(name -> genreRepository.fetchByName(name)
                        .orElseThrow(() -> new NotFoundException("Genre with name of " + name + " does not exists")))
                .collect(Collectors.toSet());

        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setIsbn(bookDTO.getIsbn());
        book.setPages(bookDTO.getPages());
        book.setPublishedDate(bookDTO.getPublishedDate());
        book.setUpdatedAt(LocalDateTime.now());
        book.setAuthor(author);
        book.setGenres(genres);

        bookRepository.save(book);
        log.debug("Book updated successfully");
    }

    public void delete(int id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id of " + id + " does not exists"));
        bookRepository.delete(book);
        log.debug("Book with id of {} deleted successfully", id);
    }

    public BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .pages(book.getPages())
                .publishedDate(book.getPublishedDate())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .authorName(book.getAuthor().getName())
                .genres(book.getGenres()
                        .stream()
                        .map(Genre::getName)
                        .toList())
                .saleCount(book.getSaleCount())
                .views(book.getViews())
                .build();
    }
}

package com.elleined.libraryapi.service.genre;

import com.elleined.libraryapi.exception.FieldAlreadyExistsException;
import com.elleined.libraryapi.exception.NotFoundException;
import com.elleined.libraryapi.model.Book;
import com.elleined.libraryapi.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreService {
    Set<Book> getAllByGenre(Genre genre);

    Genre getById(int id) throws NotFoundException;

    List<Genre> getAllById(List<Integer> ids);

    List<Genre> searchByFirstLetter(char firstLetter);

    List<Genre> getAll();

    List<Genre> getAll(int pageNumber, int pageSize);

    List<Genre> getAll(int pageNumber, int pageSize, String sortDirection, String sortProperty);

    Genre save(String genreName) throws FieldAlreadyExistsException, IllegalArgumentException;

    void update(Genre genre, String newGenreName) throws NotFoundException, FieldAlreadyExistsException, IllegalArgumentException;

    boolean isNameAlreadyExists(String genreName);
}

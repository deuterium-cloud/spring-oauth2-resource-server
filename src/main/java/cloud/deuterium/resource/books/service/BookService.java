package cloud.deuterium.resource.books.service;

import cloud.deuterium.resource.books.model.Book;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Milan Stojkovic
 */

@Service
public class BookService {

    private final Set<Book> books = new HashSet<>();

    @PostConstruct
    private void initialize() {
        books.addAll(Stream.of(
                new Book("1234567891011", "The Lord of the Rings", "John Ronald Reuel Tolkien"),
                new Book("1213141516171", "1984", "George Orwell"),
                new Book("8192021222324", "The Witcher", "Andrzej Sapkowski")
        ).toList());
    }

    public Set<Book> getAll() {
        return books;
    }

    public Book add(Book book) {
        books.add(book);
        return book;
    }

    public Book update(String isbn, Book book) {
        books.stream()
                .filter(b -> b.isbn().equals(isbn))
                .findFirst()
                .ifPresent(b -> {
                    books.remove(b);
                    books.add(book);
                });
        return book;
    }

    public void delete(String isbn) {
        books.stream()
                .filter(b -> b.isbn().equals(isbn))
                .findFirst()
                .ifPresent(books::remove);
    }
}

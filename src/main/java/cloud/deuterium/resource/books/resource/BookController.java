package cloud.deuterium.resource.books.resource;

import cloud.deuterium.resource.books.model.Book;
import cloud.deuterium.resource.books.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by Milan Stojkovic
 */

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Set<Book>> getBooks() {
        Set<Book> books = bookService.getAll();
        return ResponseEntity.ok(books);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Book> addNewBook(@RequestBody Book book) {
        Book savedBook = bookService.add(book);
        return ResponseEntity.ok(savedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book book) {
        Book updatedBook = bookService.update(isbn, book);
        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.noContent().build();
    }


}

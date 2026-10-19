package pl.kurs.biblioteka.book;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.author.AuthorShortView;

import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping
    public Page<BookResponse> getAll(Pageable pageable) {
        return bookService.findAll(pageable)
                .map(book -> BookResponse.from(book, authorService.findShortById(book.getAuthor().getId())));
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        Book book = bookService.findById(id);

        AuthorShortView authorShortView = authorService.findShortById(book.getAuthor().getId());

        return BookResponse.from(book, authorShortView);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book book = bookService.create(request);

        AuthorShortView authorShortView = authorService.findShortById(book.getAuthor().getId());

        URI location = URI.create("/books/" + book.getId());

        BookResponse bookResponse =  BookResponse.from(book, authorShortView);

        return ResponseEntity.created(location).body(bookResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}

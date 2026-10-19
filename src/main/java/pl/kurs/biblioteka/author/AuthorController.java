package pl.kurs.biblioteka.author;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.book.BookShortView;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorService.findAll(pageable)
                .map(author -> AuthorResponse.from(author, bookService.findShortByAuthorId(author.getId())
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getById(@PathVariable Long id) {
        Author author = authorService.findById(id);

        List<BookShortView> books = bookService.findShortByAuthorId(author.getId());

        return new ResponseEntity<>(AuthorResponse.from(author, books), OK);
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        Author author = authorService.create(request);

        AuthorResponse response = AuthorResponse.from(author, List.of());

        URI location = URI.create("/authors/" + author.getId());

        return ResponseEntity.created(location).body(response);
    }
}

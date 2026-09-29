package pl.kurs.biblioteka.author;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAll() {
        return ResponseEntity.ok(authorService.findAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@RequestBody @Valid AuthorRequest request) {
        AuthorResponse response = AuthorResponse.fromAuthor(authorService.create(request));
        return ResponseEntity.created(URI.create("/authors/" + response.id())).body(response);
    }
}

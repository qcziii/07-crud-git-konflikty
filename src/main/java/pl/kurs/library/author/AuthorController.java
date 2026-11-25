package pl.kurs.library.author;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorShortResponse>> getAll() {
        return new ResponseEntity<>(authorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorShortResponse> getById(@PathVariable Long id) {
        return new ResponseEntity<>(AuthorShortResponse.from(authorService.findById(id)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@RequestBody AuthorRequest request) {
        return new ResponseEntity<>(AuthorResponse.from(authorService.create(request)), HttpStatus.CREATED);
    }
}

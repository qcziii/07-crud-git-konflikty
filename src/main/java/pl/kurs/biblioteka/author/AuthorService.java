package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Nie znaleziono autora o id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(AuthorResponse::fromAuthor)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuthorResponse findAuthorById(Long id) {
        return AuthorResponse.fromAuthor(findById(id));
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        return AuthorResponse.fromAuthor(authorRepository.save(new Author(request.name())));
    }
}

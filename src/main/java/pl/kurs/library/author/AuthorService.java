package pl.kurs.library.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorShortResponse> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorShortResponse::from)
                .toList();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() ->
                        new AuthorDoesNotExistException("Author does not exist: " + id));
    }

    @Transactional
    public Author create(AuthorRequest request) {
        return authorRepository.save(new Author(request.name()));
    }
}

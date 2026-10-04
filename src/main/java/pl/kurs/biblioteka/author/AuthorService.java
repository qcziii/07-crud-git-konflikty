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
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorDoesNotExistsException("Author with id " + id + " does not exist"));
    }

    @Transactional
    public Author create(AuthorRequest request) {
        if (request.name() == null || request.name().isEmpty()) {
            throw new AuthorValidationException("Specified author name is null or empty");
        }
        return authorRepository.save(new Author(request.name()));
    }
}

package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.common.AuthorNotFoundException;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorResponse::from)
                .toList();
    }

    public AuthorResponse findById(Long id) {

        Author foundAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));

        return AuthorResponse.from(foundAuthor);
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {

        Author author = Author.builder()
                .name(request.name())
                .build();

        Author savedAuthor = authorRepository.save(author);

        return AuthorResponse.from(savedAuthor);
    }
}

package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.common.ResourceNotFoundException;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorResponse::from)
                .toList();
    }

    public AuthorResponse findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id: " + id + " do not exist"));
        return AuthorResponse.from(author);
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        return AuthorResponse.from(authorRepository.save(new Author(request.name())));
    }
}

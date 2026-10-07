package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.exception.AuthorNotFoundException;

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
        return authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author with id: " + id + " not found!"));
    }

    @Transactional
    public Author create(AuthorRequest request) {
        return authorRepository.save(new Author(request.name()));
    }
}

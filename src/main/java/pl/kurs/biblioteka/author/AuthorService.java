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

    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAllWithBooks();
    }

    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findByIdWithBooks(id).get();
    }

    @Transactional
    public Author create(AuthorRequest request) {
        return authorRepository.save(new Author(request.name()));
    }
}

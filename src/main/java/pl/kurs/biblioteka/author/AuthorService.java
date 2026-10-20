package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.mapper.AuthorMapper;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::fromAuthor)
                .toList();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id).get();
    }

    public AuthorResponse findResponseById(Long id) {
        Author author = findById(id);
        return authorMapper.fromAuthor(author);
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        Author author = new Author(request.name());
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.fromAuthor(savedAuthor);
    }
}

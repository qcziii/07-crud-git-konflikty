package pl.kurs.biblioteka.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(bookRepository.findById(id).get());
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("Author with id: " + request.authorId() + " do not exist"));
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        Book savedBook = bookRepository.save(book);
        return BookResponse.from(savedBook);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}

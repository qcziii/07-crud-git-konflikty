package pl.kurs.biblioteka.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorService;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<BookShortView> findShortByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public BookShortView findProjectedById(Long id) {
        return bookRepository.findProjectedById(id).orElseThrow();
    }

    @Transactional
    public Book create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }
        Author author = authorService.findById(request.authorId());
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new BookNotFoundException(id);

        }
        bookRepository.deleteById(id);
    }
}

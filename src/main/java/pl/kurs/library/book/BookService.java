package pl.kurs.library.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.library.author.Author;
import pl.kurs.library.author.AuthorService;
import pl.kurs.library.exception.AuthorNotFoundException;
import pl.kurs.library.exception.BookNotFoundException;
import pl.kurs.library.exception.DuplicateIsbnException;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(request.isbn());
        }
        Author author = authorService.findById(request.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.authorId()));
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }
}

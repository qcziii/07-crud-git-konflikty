package pl.kurs.biblioteka.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.common.BookNotFoundException;
import pl.kurs.biblioteka.common.ISBNDuplicatedException;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
    }

    @Transactional
    public Book create(BookRequest request) {
        Author author = authorService.findById(request.authorId());
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new ISBNDuplicatedException("Book with ISBN" + request.isbn() + " already exists.");
        }
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }
}
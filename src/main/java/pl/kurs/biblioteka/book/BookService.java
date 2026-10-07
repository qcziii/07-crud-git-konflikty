package pl.kurs.biblioteka.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.exception.BookNotFoundException;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorService authorService, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.authorRepository = authorRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book with id: " + id + " not found!"));
    }

    @Transactional
    public Book create(BookRequest request) {
        Author author = authorService.findById(request.authorId());
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with id: " + id + " not found!");
        }
        bookRepository.deleteById(id);
    }
}

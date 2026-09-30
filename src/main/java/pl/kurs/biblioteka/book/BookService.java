package pl.kurs.biblioteka.book;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {


        return bookRepository.findById(id)
                .orElseThrow(() -> new BookDoesNotExistException("book does not exist"));
    }

    @Transactional
    public Book create(BookRequest request) {
        Author author = authorService.findById(request.authorId());
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}

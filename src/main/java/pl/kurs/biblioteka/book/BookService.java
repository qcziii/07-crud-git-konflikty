package pl.kurs.biblioteka.book;

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

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookDoesNotExistException("Book with id " + id + " does not exist"));
    }

    @Transactional
    public Book create(BookRequest request) {
        if (request.title() == null || request.title().isEmpty()) {
            throw new BookValidationException("Specified book title is null or empty");
        }

        List<String> isbns = bookRepository.findAll()
                .stream()
                .map(Book::getIsbn)
                .toList();
        if (isbns.contains(request.isbn())) {
            throw new BookValidationException("Specified ISBN number already exists");
        }

        Author author = authorService.findById(request.authorId());
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new BookDoesNotExistException("Book with id " + id + " does not exist");
        }
    }
}

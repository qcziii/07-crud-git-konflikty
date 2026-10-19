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
                .orElseThrow(() -> new BookNotFoundException(String.format("Książka o id %d nie istnieje", id)));
    }

    @Transactional
    public Book create(BookRequest request) {
        Author author = authorService.findById(request.authorId());
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(String.format("Książka o ISBN %s już istnieje", request.isbn()));
        }
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.deleteById(id);
    }
}

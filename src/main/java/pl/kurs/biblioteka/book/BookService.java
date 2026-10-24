package pl.kurs.biblioteka.book;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorService;

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

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookWithIdNotFoundException(id));
    }

    @Transactional
    public Book create(BookRequest request) {
        Author author = authorService.findById(request.authorId());

        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new DuplicateOfIsbnExeption(request.isbn());
        }

        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return bookRepository.save(book);
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookWithIsbnNotFoundException(isbn));
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.findById(id).orElseThrow(() -> new BookWithIdNotFoundException(id));
        bookRepository.deleteById(id);
    }
}

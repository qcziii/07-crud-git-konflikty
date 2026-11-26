package pl.kurs.library.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.library.author.Author;
import pl.kurs.library.author.AuthorService;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findShortResponseBookById(Long id) {
        return BookResponse.from(findById(id));
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException("Cannot find book with id: " + id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        Author author = authorService.findById(request.authorId());

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException("Book with ISBN: " + request.isbn() + " already exists");
        }
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}

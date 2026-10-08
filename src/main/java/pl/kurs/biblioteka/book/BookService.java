package pl.kurs.biblioteka.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorRepository;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.common.AuthorNotFoundException;
import pl.kurs.biblioteka.common.BookNotFoundException;
import pl.kurs.biblioteka.common.DuplicateIsbnException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse findById(Long id) {

        Book foundBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        return BookResponse.from(foundBook);
    }

    @Transactional
    public BookResponse create(BookRequest request) {

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateIsbnException(
                    "Book with ISBN " + request.isbn() + " already exists"
            );
        }

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new AuthorNotFoundException(
                                "Author not found with id: " + request.authorId()
                        )
                );

        Book book = Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .availableCopies(request.availableCopies())
                .author(author)
                .build();

        Book savedBook = bookRepository.save(book);

        return BookResponse.from(savedBook);
    }

    public void delete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id
                        )
                );

        bookRepository.delete(book);
    }
}

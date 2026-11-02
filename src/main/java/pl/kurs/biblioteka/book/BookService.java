package pl.kurs.biblioteka.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.mapper.BookMapper;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, AuthorService authorService, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookMapper = bookMapper;
    }

    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::fromBook)
                .toList();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).get();
    }

    public BookResponse findResponseById(Long id) {
        Book book = findById(id);
        return  bookMapper.fromBook(book);
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        Author author = authorService.findById(request.authorId());
        Book book = new Book(request.title(), request.isbn(), request.availableCopies(), author);
        Book saveBook = bookRepository.save(book);
        return bookMapper.fromBook(saveBook);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}

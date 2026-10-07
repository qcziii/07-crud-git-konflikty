package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorDoesNotExistsException;
import pl.kurs.biblioteka.author.AuthorService;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookController;
import pl.kurs.biblioteka.book.BookDoesNotExistsException;
import pl.kurs.biblioteka.book.BookRepository;
import pl.kurs.biblioteka.book.BookRequest;
import pl.kurs.biblioteka.book.BookService;
import pl.kurs.biblioteka.book.BookValidationException;


import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void shouldGetAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn( new ArrayList<>(Arrays.asList(
            new Book("Czarny Łabędź", "0123", 2, new Author("Nassim Nicholas Taleb")),
            new Book("Potęga podświadomości", "4567", 5, new Author("Joseph Murphy"))
        )));

        mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Czarny Łabędź"))
                .andExpect(jsonPath("$[1].isbn").value("4567"));
    }

    @Test
    void shouldGetBookById() throws Exception {
        when(bookService.findById(1L)).thenReturn(new Book("Czarny Łabędź", "0123", 2, new Author("Nassim Nicholas Taleb")));

        mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Czarny Łabędź"));
    }

    @Test
    void shouldGetBookByIdAndReturn404() throws Exception {
        when(bookService.findById(99L)).thenThrow(new BookDoesNotExistsException("Book with id 99 does not exist"));

        mockMvc.perform(get("/books/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Book with id 99 does not exist"));
    }

    @Test
    void shouldCreateBookWithLocation() throws Exception {
        BookRequest request = new BookRequest("Czarny Łabędź", "0123", 2, 1L);
        Book book = new Book("Czarny Łabędź", "0123", 2, new Author("Nassim Nicholas Taleb"));

        ReflectionTestUtils.setField(book, "id", 1L);
        when(bookService.create(request)).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"title\": \"Czarny Łabędź\",\n" +
                                "    \"isbn\": \"0123\",\n" +
                                "    \"availableCopies\": 2,\n" +
                                "    \"authorId\": 1\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Czarny Łabędź"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(header().string("Location", "http://localhost/books/1"));
    }

    @Test
    void shouldCreateBookWithLocationAndReturn404() throws Exception {
        BookRequest request = new BookRequest("Czarny Łabędź", "0123", 2, 1L);
        when(bookService.create(request)).thenThrow(new AuthorDoesNotExistsException("Author with id 99 does not exist"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"title\": \"Czarny Łabędź\",\n" +
                                "    \"isbn\": \"0123\",\n" +
                                "    \"availableCopies\": 2,\n" +
                                "    \"authorId\": 1\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Author with id 99 does not exist"));
    }

    @Test
    void shouldCreateBookWithLocationAndReturn409() throws Exception {
        BookRequest request = new BookRequest("Czarny Łabędź", "0123", 2, 1L);
        when(bookService.create(request)).thenThrow(new BookValidationException("Specified ISBN number already exists"));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"title\": \"Czarny Łabędź\",\n" +
                                "    \"isbn\": \"0123\",\n" +
                                "    \"availableCopies\": 2,\n" +
                                "    \"authorId\": 1\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.Error").value("Specified ISBN number already exists"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteBookAndReturn404() throws Exception {
        doThrow(new BookDoesNotExistsException("Book with id 99 does not exist")).when(bookService).delete(99L);

        mockMvc.perform(delete("/books/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Book with id 99 does not exist"));
    }
}

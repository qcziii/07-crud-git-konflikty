package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.biblioteka.author.AuthorDetailsResponse;
import pl.kurs.biblioteka.author.AuthorNotFoundException;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private BookResponse solaris;
    private BookResponse bieguni;

    @BeforeEach
    void init() {
        solaris = new BookResponse(1L, "Solaris", "978-83-08-04907-1", 2,
                new AuthorDetailsResponse(1L, "Stanisław Lem"));
        bieguni = new BookResponse(3L, "Bieguni", "978-83-08-04151-8", 5,
                new AuthorDetailsResponse(2L, "Olga Tokarczuk"));
    }

    @Test
    void shouldGetAllBooks() throws Exception {
        when(bookService.findAllBooks()).thenReturn(List.of(solaris, bieguni));

        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[0].availableCopies").value(2))
                .andExpect(jsonPath("$[0].author.id").value(1))
                .andExpect(jsonPath("$[0].author.name").value("Stanisław Lem"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].title").value("Bieguni"))
                .andExpect(jsonPath("$[1].isbn").value("978-83-08-04151-8"))
                .andExpect(jsonPath("$[1].availableCopies").value(5))
                .andExpect(jsonPath("$[1].author.id").value(2))
                .andExpect(jsonPath("$[1].author.name").value("Olga Tokarczuk"));

        verify(bookService, times(1)).findAllBooks();
    }

    @Test
    void shouldGetBookById() throws Exception {
        when(bookService.findBookById(1L)).thenReturn(solaris);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.author.name").value("Stanisław Lem"));

        verify(bookService, times(1)).findBookById(1L);
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.findBookById(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono książki o id: 99"));

        verify(bookService, times(1)).findBookById(99L);
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookService.create(any(BookRequest.class))).thenReturn(solaris);

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Solaris",
                                    "isbn": "978-83-08-04907-1",
                                    "availableCopies": 2,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/books/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.author.name").value("Stanisław Lem"));

        verify(bookService, times(1)).create(any(BookRequest.class));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "isbn": "978-83-08-04907-1",
                                    "availableCopies": 2,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("title"));

        verify(bookService, never()).create(any(BookRequest.class));
    }

    @Test
    void shouldReturn404WhenAuthorNotFoundOnCreate() throws Exception {
        when(bookService.create(any(BookRequest.class)))
                .thenThrow(new AuthorNotFoundException("Nie znaleziono autora o id: 99"));

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Solaris",
                                    "isbn": "978-83-08-04907-1",
                                    "availableCopies": 2,
                                    "authorId": 99
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono autora o id: 99"));

        verify(bookService, times(1)).create(any(BookRequest.class));
    }

    @Test
    void shouldReturn409WhenIsbnIsDuplicated() throws Exception {
        when(bookService.create(any(BookRequest.class)))
                .thenThrow(new DuplicateIsbnException("Duplikacja isbn: 978-83-08-04907-1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Solaris",
                                    "isbn": "978-83-08-04907-1",
                                    "availableCopies": 2,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Duplikacja isbn: 978-83-08-04907-1"));

        verify(bookService, times(1)).create(any(BookRequest.class));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        doThrow(new BookNotFoundException(99L)).when(bookService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono książki o id: 99"));

        verify(bookService, times(1)).delete(99L);
    }
}
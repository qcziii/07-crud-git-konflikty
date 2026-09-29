package pl.kurs.biblioteka.author;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.biblioteka.book.BookResponseDetails;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    private AuthorResponse lem;
    private AuthorResponse tokarczuk;

    @BeforeEach
    void init() {
        lem = new AuthorResponse(1L, "Stanisław Lem",
                List.of(new BookResponseDetails(1L, "Solaris", "978-83-08-04907-1", 2)));
        tokarczuk = new AuthorResponse(2L, "Olga Tokarczuk", List.of());
    }

    @Test
    void shouldGetAllAuthors() throws Exception {
        when(authorService.findAllAuthors()).thenReturn(List.of(lem, tokarczuk));

        mockMvc.perform(MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Stanisław Lem"))
                .andExpect(jsonPath("$[0].books", hasSize(1)))
                .andExpect(jsonPath("$[0].books[0].id").value(1))
                .andExpect(jsonPath("$[0].books[0].title").value("Solaris"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Olga Tokarczuk"))
                .andExpect(jsonPath("$[1].books", hasSize(0)));

        verify(authorService, times(1)).findAllAuthors();
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        when(authorService.findAuthorById(1L)).thenReturn(lem);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Stanisław Lem"))
                .andExpect(jsonPath("$.books[0].id").value(1))
                .andExpect(jsonPath("$.books[0].title").value("Solaris"));

        verify(authorService, times(1)).findAuthorById(1L);
    }

    @Test
    void shouldReturn404WhenAuthorNotFound() throws Exception {
        when(authorService.findAuthorById(99L))
                .thenThrow(new AuthorNotFoundException("Nie znaleziono autora o id: 99"));

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nie znaleziono autora o id: 99"));

        verify(authorService, times(1)).findAuthorById(99L);
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorResponse sapkowski = new AuthorResponse(3L, "Andrzej Sapkowski", List.of());
        when(authorService.create(any(AuthorRequest.class))).thenReturn(sapkowski);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Andrzej Sapkowski"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/authors/3"))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Andrzej Sapkowski"))
                .andExpect(jsonPath("$.books", hasSize(0)));

        verify(authorService, times(1)).create(any(AuthorRequest.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("name"));

        verify(authorService, never()).create(any(AuthorRequest.class));
    }
}
package pl.kurs.biblioteka.book;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldFindAllBooks() throws Exception {
        postman.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].author.id").exists())
                .andExpect(jsonPath("$[0].author.name").exists());
    }

    @Test
    void shouldReturn200WithBookAndItsAuthor() throws Exception {
        postman.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.isbn").isNotEmpty())
                .andExpect(jsonPath("$.author.id").exists())
                .andExpect(jsonPath("$.author.name").isNotEmpty());
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        postman.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldReturn201WithLocationWhenBookCreated() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "777-666-00", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/books/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.isbn").value("777-666-00"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andExpect(jsonPath("$.author.id").value(1));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {

        BookRequest bookRequest = new BookRequest("", "777-666-00", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExistOnCreate() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "978-83-00-00000-3", 2, 999L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldReturn409WhenIsbnIsDuplicated() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "978-83-00-00000-4", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isCreated());

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value(not(containsString("SQL"))));
    }

    @Test
    void shouldReturn204WhenBookDeleted() throws Exception {

        BookRequest bookRequest = new BookRequest("Test", "978-83-00-00000-5", 2, 1L);

        String responseBody = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdId = objectMapper.readValue(responseBody, BookResponse.class).id();

        postman.perform(delete("/books/{id}", createdId))
                .andExpect(status().isNoContent());

        postman.perform(get("/books/{id}", createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        postman.perform(delete("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
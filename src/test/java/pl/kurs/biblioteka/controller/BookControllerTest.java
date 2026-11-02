package pl.kurs.biblioteka.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.biblioteka.BibliotekaApplication;
import pl.kurs.biblioteka.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BibliotekaApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql")
public class BookControllerTest {

    private final MockMvc postman;
    private final ObjectMapper objectMapper;

    @Autowired
    public BookControllerTest(MockMvc postman, ObjectMapper objectMapper) {
        this.postman = postman;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        postman.perform(get("/books/1000"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book with id: 1000 not found"))
                .andExpect(jsonPath("$.uri").value("/books/1000"))
                .andExpect(jsonPath("$.method").value("GET"));

    }

    @Test
    void shouldReturn200WhenBookExists() throws Exception {
        postman.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void shouldReturn201AndLocationWhenBookIsCreated() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "9788300000001", 2, 1L);

        MvcResult result = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Pan Tadeusz"))
                .andExpect(jsonPath("$.isbn").value("9788300000001"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andReturn();

        assertNotNull(result.getResponse().getHeader("Location"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        BookRequest request = new BookRequest("", "9788300000001", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Title can not be empty"))
                .andExpect(jsonPath("$.uri").value("/books"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "9788300000002", 2, 999L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Author with id: 999 not found"))
                .andExpect(jsonPath("$.uri").value("/books"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldReturn409WhenIsbnAlreadyExists() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "978-83-08-04907-1", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.status").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Book with this ISBN already exists"))
                .andExpect(jsonPath("$.uri").value("/books"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    void shouldReturn204WhenBookIsDeleted() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "97883000001233", 2, 1L);

        MvcResult result = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Pan Tadeusz"))
                .andExpect(jsonPath("$.isbn").value("97883000001233"))
                .andExpect(jsonPath("$.availableCopies").value(2))
                .andReturn();

        String location = result.getResponse().getHeader("Location");

        assert location != null;
        postman.perform(delete(location))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        postman.perform(delete("/books/999999"))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book with id: 999999 not found"))
                .andExpect(jsonPath("$.uri").value("/books/999999"))
                .andExpect(jsonPath("$.method").value("DELETE"));
    }
}

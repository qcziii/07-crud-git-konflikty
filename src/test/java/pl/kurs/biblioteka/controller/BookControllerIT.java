package pl.kurs.biblioteka.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.kurs.biblioteka.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest("BibliotekaApplication.class")
@AutoConfigureMockMvc
public class BookControllerIT {

    private final MockMvc postman;
    private final ObjectMapper objectMapper;

    @Autowired
    public BookControllerIT(MockMvc postman, ObjectMapper objectMapper) {
        this.postman = postman;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        postman.perform(get("/books/1000"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200WhenBookExists() throws Exception {
        postman.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk());
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
                .andReturn();

        assertNotNull(result.getResponse().getHeader("Location"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        BookRequest request = new BookRequest("","9788300000001",2,1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenAuthorDoesNotExist() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "9788300000002", 2, 999L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenIsbnAlreadyExists() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "9788300000123", 2, 1L);

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());

        postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn204WhenBookIsDeleted() throws Exception {
        BookRequest request = new BookRequest("Pan Tadeusz", "97883000001233", 2, 1L);

        MvcResult result = postman.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
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
                .andExpect(status().isNotFound());
    }
}

package pl.kurs.biblioteka.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.biblioteka.book.BookRequest;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTests {
    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSaveBook() throws Exception {
        BookRequest bookRequest = new BookRequest("test", "test", 10, 1L);

        postman.perform(MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.isbn").value("test"))
                .andExpect(jsonPath("$.availableCopies").value(10))
                .andExpect(jsonPath("$.author.id").value(1L));
    }

    @Test
    void shouldSaveBookDuplicateIsbn() throws Exception {
        BookRequest bookRequest = new BookRequest("test", "978-83-08-04907-1", 10, 1L);

        postman.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void shouldSaveBookAuthorDoesNotExist() throws Exception {
        BookRequest bookRequest = new BookRequest("test", "123", 10, 999L);

        postman.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBook() throws Exception {

        postman.perform(MockMvcRequestBuilders.delete("/books/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        postman.perform(MockMvcRequestBuilders.get("/books/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}

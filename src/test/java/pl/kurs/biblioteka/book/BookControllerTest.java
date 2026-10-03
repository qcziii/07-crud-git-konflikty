package pl.kurs.biblioteka.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
    private MockMvc postman;
    private ObjectMapper objectMapper;

    @Autowired
    public BookControllerTest(MockMvc postman, ObjectMapper objectMapper) {
        this.postman = postman;
        this.objectMapper = objectMapper;
    }
    @Test
    void shouldGetAllBooks() throws Exception {
        // given \ when \ then
        postman.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Solaris"))
                .andExpect(jsonPath("$[0].isbn").value("978-83-08-04907-1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Cyberiada"))
                .andExpect(jsonPath("$[1].isbn").value("978-83-08-06175-2"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("Bieguni"))
                .andExpect(jsonPath("$[2].isbn").value("978-83-08-04151-8"));
    }

    @Test
    void getBookById() throws Exception {
        // given \ when \ then
        postman.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Solaris"))
                .andExpect(jsonPath("$.isbn").value("978-83-08-04907-1"));
    }
}
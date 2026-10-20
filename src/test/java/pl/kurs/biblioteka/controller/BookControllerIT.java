package pl.kurs.biblioteka.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest("BibliotekaApplication.class")
@AutoConfigureMockMvc
public class BookControllerIT {

    private final MockMvc postman;

    @Autowired
    public BookControllerIT(MockMvc postman) {
        this.postman = postman;
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
}

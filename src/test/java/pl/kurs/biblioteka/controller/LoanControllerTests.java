package pl.kurs.biblioteka.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.biblioteka.loan.LoanRequest;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTests {
    @Autowired
    MockMvc postman;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @Order(1)
    void shouldSaveLoan() throws Exception {
        LoanRequest loanRequest = new LoanRequest(1L, "jan@gmail.com");

        postman.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @Order(2)
    void shouldGetByReader() throws Exception {
        postman.perform(MockMvcRequestBuilders.get("/loans?readerEmail=jan@gmail.com"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    void shouldthorw404IfNotExist() throws Exception {
        LoanRequest loanRequest = new LoanRequest(999L, "test@gmail.com");

        postman.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(4)
    void shouldThrow409IfLimitExceeded() throws Exception {
        LoanRequest loanRequest = new LoanRequest(4L, "test@gmail.com");

        for (int i = 0; i < 3; i++) {
            postman.perform(MockMvcRequestBuilders.post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanRequest)))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        postman.perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @Order(5)
    void booksAvailabilityShouldStayTheSameIfLimitExceeded() throws Exception {
        LoanRequest loanRequest = new LoanRequest(3L, "limit@gmail.com");

        for (int i = 0; i < 5; i++) {
            postman.perform(MockMvcRequestBuilders.post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanRequest)))
                    .andDo(print());
        }

        postman.perform(MockMvcRequestBuilders.get("/books/3"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.availableCopies").value(2));
    }
}



package pl.kurs.library.loan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest("LibraryApplication.class")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoanControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldBorrow() throws Exception {
        //given
        String readerEmail = "aaa@gmail.com";
        LoanRequest request = new LoanRequest(1L, readerEmail);

        postman.perform(get("/books/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));

        //when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.book.title").value("Solaris"))
                .andExpect(jsonPath("$.readerEmail").value(readerEmail))
                .andExpect(jsonPath("$.loanDate").value(LocalDate.now().toString()));

        //then
        postman.perform(get("/loans")
                        .param("readerEmail", readerEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.id").value(1))
                .andExpect(jsonPath("$[0].book.title").value("Solaris"))
                .andExpect(jsonPath("$[0].readerEmail").value(readerEmail))
                .andExpect(jsonPath("$[0].loanDate").value(LocalDate.now().toString()));

        postman.perform(get("/books/{1}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));
    }

    @Test
    void shouldNotBorrowBookWhenEmailIsInvalid() throws Exception {
        //given
        String readerEmail = "aaa";
        LoanRequest request = new LoanRequest(1L, readerEmail);
        //when + then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotBorrowIfBookDoesNotExist() throws Exception {
        //given
        String readerEmail = "aaa@gmail.com";
        LoanRequest request = new LoanRequest(5L, readerEmail);

        postman.perform(get("/books/{id}", 5))
                .andDo(print())
                .andExpect(status().isNotFound());

        //when
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

    }

    @Test
    void shouldNotBorrowWhenNoAvailableCopies() throws Exception {
        //given
        LoanRequest request = new LoanRequest(1L, "aaa@gmail.com");

        for (int i = 0; i < 2; i++) {
            postman.perform(post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        //when + then
        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldGetByReader() throws Exception {
        //given
        String readerEmail = "aaa@gmail.com";
        LoanRequest request = new LoanRequest(1L, readerEmail);

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.book.title").value("Solaris"))
                .andExpect(jsonPath("$.readerEmail").value(readerEmail))
                .andExpect(jsonPath("$.loanDate").value(LocalDate.now().toString()));

        //when + then
        postman.perform(get("/loans")
                        .param("readerEmail", readerEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.id").value(1))
                .andExpect(jsonPath("$[0].book.title").value("Solaris"))
                .andExpect(jsonPath("$[0].readerEmail").value(readerEmail))
                .andExpect(jsonPath("$[0].loanDate").value(LocalDate.now().toString()));
    }


    @Test
    void shouldGiveBack() throws Exception {

        //given

        String readerEmail = "aaa@gmail.com";
        LoanRequest request = new LoanRequest(1L, readerEmail);

        postman.perform(get("/books/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));

        postman.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(1));

        //when + then
        long id = 1;
        postman.perform(delete("/loans/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldNotGiveBackWhenLoanDoesNotExist() throws Exception {
        //given
        long id = 999L;

        //when + then
        postman.perform(delete("/loans/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
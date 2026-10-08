package pl.kurs.biblioteka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiHttpStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAuthorsAndBooksReturn200() throws Exception {
        mockMvc.perform(get("/authors")).andExpect(status().isOk());
        mockMvc.perform(get("/books")).andExpect(status().isOk());
    }

    @Test
    void getExistingAuthorAndBookReturn200() throws Exception {
        mockMvc.perform(get("/authors/1")).andExpect(status().isOk());
        mockMvc.perform(get("/books/1")).andExpect(status().isOk());
    }

    @Test
    void getMissingAuthorAndBookReturn404() throws Exception {
        mockMvc.perform(get("/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").exists());
        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.blad").exists());
    }

    @Test
    void postAuthorReturns201WithLocation() throws Exception {
        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Wisława Szymborska\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/authors/")));
    }

    @Test
    void postAuthorWithBlankNameReturns400() throws Exception {
        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").value("Imię autora nie może być puste"));
    }

    @Test
    void postBookReturns201WithLocation() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Pokój","isbn":"978-83-00-00000-1","availableCopies":2,"authorId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/books/")));
    }

    @Test
    void postBookWithEmptyTitleReturns400() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","isbn":"978-99-00-00000-1","availableCopies":1,"authorId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.blad").value("Tytuł nie może być pusty"));
    }

    @Test
    void postBookWithMissingAuthorReturns404() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Nieznany autor","isbn":"978-77-00-00000-1","availableCopies":1,"authorId":999}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void postBookWithDuplicateIsbnReturns409WithoutSql() throws Exception {
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Kopia Solarisa","isbn":"978-83-08-04907-1","availableCopies":1,"authorId":1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").exists())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).doesNotContain("SQL");
        assertThat(body).doesNotContain("insert into");
        assertThat(body).doesNotContain("CONSTRAINT");
    }

    @Test
    void deleteMissingBookReturns404() throws Exception {
        mockMvc.perform(delete("/books/999")).andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingBookReturns204() throws Exception {
        MvcResult created = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Do usunięcia","isbn":"978-66-00-00000-1","availableCopies":1,"authorId":1}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
    }

    @Test
    void getLoansReturns200() throws Exception {
        mockMvc.perform(get("/loans").param("readerEmail", "nobody@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void postLoanReturns201WithLocation() throws Exception {
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":3,\"readerEmail\":\"status@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/loans/")));
    }

    @Test
    void postLoanWithInvalidDataReturns400() throws Exception {
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":3,\"readerEmail\":\"nie-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLoanForMissingBookReturns404() throws Exception {
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":999,\"readerEmail\":\"brak@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postLoanWhenNoCopiesReturns409() throws Exception {
        MvcResult created = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Ostatni egzemplarz","isbn":"978-55-00-00000-1","availableCopies":0,"authorId":1}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String bookId = created.getResponse().getHeader("Location")
                .replaceFirst(".*/", "");

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + bookId + ",\"readerEmail\":\"brak-egz@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.blad").value(containsString("Brak wolnych egzemplarzy")));
    }

    @Test
    void deleteMissingLoanReturns404() throws Exception {
        mockMvc.perform(delete("/loans/999")).andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingLoanReturns204() throws Exception {
        MvcResult created = mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":3,\"readerEmail\":\"zwrot@example.com\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
    }
}

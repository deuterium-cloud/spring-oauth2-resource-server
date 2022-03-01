package cloud.deuterium.resource.books;

import cloud.deuterium.resource.books.config.SecurityConfig;
import cloud.deuterium.resource.books.model.Book;
import cloud.deuterium.resource.books.resource.BookController;
import cloud.deuterium.resource.books.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Milan Stojkovic
 *
 * .with(csrf().asHeader()) -> if csrf is not disabled!
 */

@Import(SecurityConfig.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtDecoder jwtDecoder;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Should return 401 Unauthorized")
    void getBooks_401() throws Exception {
        this.mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 200 OK")
    void getBooks_200() throws Exception {

        mockMvc
                .perform(get("/books")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("Should return 200 OK")
    void getBooks_with_mockedUser() throws Exception {

        mockMvc
                .perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 UNAUTHORIZED because User has USER role")
    void addNewBook_UserRole() throws Exception {

        Book chemistry = new Book("978-1319079451", "Ogranic Chemistry", "Vollhardt");
        String json = objectMapper.writeValueAsString(chemistry);

        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 200 OK because User has ADMIN role")
    void addNewBook_AdminRole() throws Exception {

        Book chemistry = new Book("978-1319079451", "Ogranic Chemistry", "Vollhardt");
        String json = objectMapper.writeValueAsString(chemistry);

        mockMvc
                .perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

}

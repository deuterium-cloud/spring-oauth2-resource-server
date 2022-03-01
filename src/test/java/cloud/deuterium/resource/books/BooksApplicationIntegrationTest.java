package cloud.deuterium.resource.books;

import cloud.deuterium.resource.books.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksApplicationIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should return 401 because no Authorization header is present")
    public void no_token() {

        ResponseEntity<Object> response = this.restTemplate
                .exchange("/books", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 because Bearer token is expired")
    public void expired_token() {

        String EXPIRED_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhcHAiOiJib29rLXN0b3JlIiwic3ViIjoiam9obi5kb2VAdGVzdG1haWwuY29tIiwiYXVkIjoiZGV1dGVyaXVtIiwibmJmIjoxNjQ1MjAyNzA0LCJzY29wZSI6WyJvcGVuaWQiXSwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIiLCJpZCI6ImM0ZTIxZmZmLTNmMzUtNDI3Zi1iNWE4LTQ3Y2VmZjVkMDZhMyIsImV4cCI6MTY0NTIwMzAwNCwiaWF0IjoxNjQ1MjAyNzA0LCJlbWFpbCI6ImpvaG4uZG9lQHRlc3RtYWlsLmNvbSJ9.MlnLd5plP4fEPv626fUJmmO81NpCWb4SbUfo_wDV5cYVWHluN3gv5nFziFyQQIcv5-bAnukwvJO9oR9Wd8Z1ZMkYKyqVinD1lQq4jfvfevqbBJ4rji5-EhXHfdMHCDi6R3OyX0bcSdQT87u5eqT-U3BF-C54kP_wx9gP9ZRvZw9EapfqD00KoaDUJN775B1Lz9-yuBrGIAZjNAY4TFPNJAk5ROQHI0x77h2dKfNmPBn2ZFQtXyzA83oFaoopcGXJQi7TazVqQW2z52DfS1Uy6QN6IZQaM7Uxpj68oPfODkjEIKu4gSdX4ENXgtSS_cLBefQGv6jL7xX2eScFhIFDcQ";
        ResponseEntity<Object> response = restTemplate
                .exchange("/books", HttpMethod.GET, new HttpEntity<>(getAuthorizationBearerHeader(EXPIRED_JWT)), new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 because Bearer token is not signed by valid key")
    public void forged_token() {

        String FORGED_JWT = "eyJraWQiOiIyYWJiMWNmYy05MGI4LTRhYTEtODM0OC1jYzM3ZmE1ZGQ2YTciLCJhbGciOiJSUzI1NiJ9.eyJhcHAiOiJlY2hlbG9uIiwic3ViIjoidGVzdENMaWVudDVAZ21haWwuY29tIiwiYXVkIjoiZWNoZWxvbiIsIm5iZiI6MTY0NTIwMjcwNCwic2NvcGUiOlsib3BlbmlkIl0sInJvbGVzIjpbIlVTRVIiXSwiaXNzIjoiaHR0cDpcL1wvbG9jYWxob3N0OjgwODIiLCJpZCI6ImM0ZTIxZmZmLTNmMzUtNDI3Zi1iNWE4LTQ3Y2VmZjVkMDZhMyIsImV4cCI6MTY0NTIwMzAwNCwiaWF0IjoxNjQ1MjAyNzA0LCJlbWFpbCI6InRlc3RDTGllbnQ1QGdtYWlsLmNvbSJ9.sDQPdtLrVIcsD77VIelyNaVISGdRGYADKrqW8AerRNf0wFwIWSm5PHahiFi7YE2bysYmaprwklAhHy4HMX0t1-cnVVpOOvj6uSsb-UYzBOQV4b3wPmApPBkS-7h0abugHQ1UmADB9c_t3Y7Z4ovoy9KtbwVvjPeut6qmMQQVskfEIGNfsPj733uwh4Q9COPZMLxBrKU5o0qNlVX6BGWdISZA3jIGl5zi8sXKwfWpsxdU27XGYPDhtqwMY7CHU8ebolDtbG5vLF-CUjMn27Byb1HfL2Br0sMrlfuPXUfMH6P-XfWwpCFPkl8qlPOS6h8HLJcze0bIRp0aQeyw5CqACg";
        ResponseEntity<Object> response = restTemplate
                .exchange("/books", HttpMethod.GET, new HttpEntity<>(getAuthorizationBearerHeader(FORGED_JWT)), new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 200 because valid Bearer token is present")
    public void valid_token_getAll() {

        String VALID_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhcHAiOiJib29rLXN0b3JlIiwic3ViIjoiam9obi5kb2VAdGVzdG1haWwuY29tIiwiYXVkIjoiZGV1dGVyaXVtIiwibmJmIjoxNjQ1MjAyNzA0LCJzY29wZSI6WyJvcGVuaWQiXSwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIiLCJpZCI6ImM0ZTIxZmZmLTNmMzUtNDI3Zi1iNWE4LTQ3Y2VmZjVkMDZhMyIsImV4cCI6MjY0NTIwMzAwNCwiaWF0IjoxNjQ1MjAyNzA0LCJlbWFpbCI6ImpvaG4uZG9lQHRlc3RtYWlsLmNvbSJ9.cZjIdYhgMwpx_URO41O--CMduQNtoGEu-zk4TAgakfmYOKKUv968k648wmxpBRW-QPZizW6biHm_smkVWu8E0LPdmMGdwZ5jUEmg0468_wUUeGW5v0mBOIKPf4naF1PcETREcEduonp2Cb8NcpVZOt9DKfAan8bSQ9d3W4_9wy2nYAYeb4mGqzcRIGKRUd0H3Ft08dCwfwRVDNCleqHzSbluiQshYKtDVvkvg92tNX8kI9c2zVK5cKkNNO7Pe-aJWOcRenzYih9IkqGYxxPJajQaO_YXey76fxuL9q_MkW_k4y2HB5IVM8GQ0WmDOjUmy1J76k5p8ZcX1dAAEIPmeA";
        ResponseEntity<Object> response = restTemplate
                .exchange("/books", HttpMethod.GET, new HttpEntity<>(getAuthorizationBearerHeader(VALID_JWT)), new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should return 403 because user has no ADMIN role")
    public void no_ADMIN_role() {

        String VALID_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhcHAiOiJib29rLXN0b3JlIiwic3ViIjoiam9obi5kb2VAdGVzdG1haWwuY29tIiwiYXVkIjoiZGV1dGVyaXVtIiwibmJmIjoxNjQ1MjAyNzA0LCJzY29wZSI6WyJvcGVuaWQiXSwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIiLCJpZCI6ImM0ZTIxZmZmLTNmMzUtNDI3Zi1iNWE4LTQ3Y2VmZjVkMDZhMyIsImV4cCI6MjY0NTIwMzAwNCwiaWF0IjoxNjQ1MjAyNzA0LCJlbWFpbCI6ImpvaG4uZG9lQHRlc3RtYWlsLmNvbSJ9.cZjIdYhgMwpx_URO41O--CMduQNtoGEu-zk4TAgakfmYOKKUv968k648wmxpBRW-QPZizW6biHm_smkVWu8E0LPdmMGdwZ5jUEmg0468_wUUeGW5v0mBOIKPf4naF1PcETREcEduonp2Cb8NcpVZOt9DKfAan8bSQ9d3W4_9wy2nYAYeb4mGqzcRIGKRUd0H3Ft08dCwfwRVDNCleqHzSbluiQshYKtDVvkvg92tNX8kI9c2zVK5cKkNNO7Pe-aJWOcRenzYih9IkqGYxxPJajQaO_YXey76fxuL9q_MkW_k4y2HB5IVM8GQ0WmDOjUmy1J76k5p8ZcX1dAAEIPmeA";
        Book chemistry = new Book("978-1319079451", "Ogranic Chemistry", "Vollhardt");
        HttpEntity<Book> requestEntity = new HttpEntity<>(chemistry, getAuthorizationBearerHeader(VALID_JWT));

        ResponseEntity<Object> response = restTemplate
                .exchange("/books", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should return 200 because user has ADMIN role")
    public void with_ADMIN_role() {

        String VALID_JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhcHAiOiJib29rLXN0b3JlIiwic3ViIjoiam9obi5kb2VAdGVzdG1haWwuY29tIiwiYXVkIjoiZGV1dGVyaXVtIiwibmJmIjoxNjQ1MjAyNzA0LCJzY29wZSI6WyJvcGVuaWQiXSwicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiaWQiOiJjNGUyMWZmZi0zZjM1LTQyN2YtYjVhOC00N2NlZmY1ZDA2YTMiLCJleHAiOjE4NDUyMDMwMDQsImlhdCI6MTY0NTIwMjcwNCwiZW1haWwiOiJqb2huLmRvZUB0ZXN0bWFpbC5jb20ifQ.M2O4IDcdm_8hZC21uvMUQzWrxhLDuDf2795wB-Pqjb1n752rf2OKkSpVUhD060kCpwtZSp0GOaYUR3E9sfgYD9uFqVkx54C9ylcURzemyJHnk5MLm-i55NzWlmEfZaTDbvPSJAnp4M3Iz4_PZ98CPWSyicNCjA83dYzn911EUwnyxh9v9q_08XEy9PZTaUtg6x_p_hItpamV3V6PnTrYHLgDMeTMJo2Q-0KwrDveLcVqjHFThNH0pMlOwoqCIvOovYzHXGl59f8hj-LzoyYAsa0QGY3Qo-4CNiEaHocLw7HTY9dF5uUtbp6j2bivO4NOkl5mSf39YvhnCmvyjmBwCg";
        Book chemistry = new Book("978-1319079451", "Ogranic Chemistry", "Vollhardt");
        HttpEntity<Book> requestEntity = new HttpEntity<>(chemistry, getAuthorizationBearerHeader(VALID_JWT));

        ResponseEntity<Object> response = restTemplate
                .exchange("/books", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpHeaders getAuthorizationBearerHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        return httpHeaders;
    }

}

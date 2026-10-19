package com.careerflow.backend.support;

import com.careerflow.backend.model.User;
import com.careerflow.backend.model.enums.Role;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/** Small helper so integration tests can act as a given user with a valid JWT. */
public class TestApi {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public TestApi(MockMvc mockMvc, ObjectMapper mapper, UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /** Creates a user and returns its bearer header value. */
    public String newUserToken(String email) {
        User user = users.save(User.builder()
                .firstName("Test").lastName("User").email(email)
                .password(encoder.encode("password123")).role(Role.USER).build());
        return "Bearer " + jwt.generateToken(user);
    }

    public ResultActions call(String method, String url, String token, Object body) throws Exception {
        MockHttpServletRequestBuilder req = request(org.springframework.http.HttpMethod.valueOf(method), url)
                .header(HttpHeaders.AUTHORIZATION, token);
        if (body != null) {
            req.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body));
        }
        return mockMvc.perform(req);
    }

    /** POSTs a body and returns data.id from the response envelope. */
    public long createAndGetId(String url, String token, Object body) throws Exception {
        String json = call("POST", url, token, body).andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).path("data").path("id").asLong();
    }

    public static Map<String, Object> application(String company) {
        return Map.of("companyName", company, "position", "Engineer", "status", "APPLIED");
    }
}

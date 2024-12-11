package com.diplom.cloudstorage.controllerTest;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.entity.File;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.repository.FilesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FilesRepository filesRepository;

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/list?limit=3"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getFilesListWithLimitTest() throws Exception {
        List<File> fileList = List.of(
                File.builder().id(1L).filename("test.txt").createdAt(LocalDate.now()).size(1).fileContent("test".getBytes()).user(new User(1L, "user@user.user", "user")).build(),
                File.builder().id(2L).filename("test2.txt").createdAt(LocalDate.now()).size(2).fileContent("test2".getBytes()).user(new User(1L, "user@user.user", "user")).build(),
                File.builder().id(3L).filename("test3.txt").createdAt(LocalDate.now()).size(1).fileContent("test3".getBytes()).user(new User(1L, "user@user.user", "user")).build());

        User user = new User(1L, "user@user.user", "user");

        Mockito.when(this.filesRepository.getFilesByUserWithLimit(user.getId(), 3))
                .thenReturn(fileList);
        AuthRequest request = AuthRequest.builder()
                .login("user@user.user")
                .password("user")
                .build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        String resultStr = mvcResult.getResponse().getContentAsString();

        AuthResponse token = new ObjectMapper().readValue(resultStr, AuthResponse.class);
        mockMvc.perform(get("/list")
                        .header("auth-token", "Bearer " + token.getToken())
                        .param("limit", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(fileList)));
    }

    @Test
    void testWithInvalidBearerToken() throws Exception {
        mockMvc.perform(get("/list?limit=3")
                        .header("auth-token", "Bearer 123456789"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}


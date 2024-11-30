package com.diplom.cloudstorage.controllerTest;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginWithValidCredentials() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .login("user@user.user")
                .password("user")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithInvalidCredentials() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .login("admin@admin.admin")
                .password("admin")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is(400));// Bad Credentials
    }

    @Test
    public void logout() throws Exception {
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
        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .header("auth-token", "Bearer " + token.getToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

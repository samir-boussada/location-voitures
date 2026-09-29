package com.locationvoitures.login;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageEstAccessibleSansAuthentification() throws Exception {
        mockMvc.perform(get("/login.html"))
                .andExpect(status().isOk());
    }

    @Test
    void loginApresAuthentificationRedirigeVersApplication() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("triztech123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginAvecMauvaisMotDePasseRedirigeVersErreur() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("mauvais"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login.html?error"));
    }

    @Test
    void pageProtegeeRedirigeVersLoginSansSession() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    void logoutRedirigeEtFermeLaSession() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(status().is3xxRedirection());
    }
}
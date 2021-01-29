package com.upgrad.quora.api.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    String bodyForRepeatedUserNameTest = "{" +
            "\"first_name\":\"a\"," +
            "\"last_name\":\"a\"," +
            "\"user_name\":\"database_username\"," +
            "\"email_address\":\"a\"," +
            "\"password\":\"a\"," +
            "\"country\":\"a\"," +
            "\"aboutMe\":\"a\"," +
            "\"dob\":\"a\"," +
            "\"contact_number\":\"a\"" +
            "}";

    String bodyForRepeatedEmailTest =  "{" +
            "\"first_name\":\"a\"," +
            "\"last_name\":\"a\"," +
            "\"user_name\":\"non_existing_username\"," +
            "\"email_address\":\"database_email\"," +
            "\"password\":\"a\"," +
            "\"country\":\"a\"," +
            "\"aboutMe\":\"a\"," +
            "\"dob\":\"a\"," +
            "\"contact_number\":\"a\"" +
            "}";

    @Autowired
    private MockMvc mvc;

    //This test case passes when you signup with a username that already exists in the database.
    @Test
    public void signupWithRepeatedUserName() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/signup").content(bodyForRepeatedUserNameTest).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("SGR-001"));
    }

    //This test case passes when you signup with an email that already exists in the database.
    @Test
    public void signupWithRepeatedEmail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/signup").content(bodyForRepeatedEmailTest).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("SGR-002"));
    }


    //This test case passes when you try to signout but the JWT token entered does not exist in the database.
    @Test
    public void signoutWithNonExistingAccessToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/signout").header("authorization", "non_existing_access_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("SGR-001"));
    }
}

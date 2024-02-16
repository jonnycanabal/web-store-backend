package com.web.store.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.store.entity.User;
import com.web.store.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ObjectMapper objectMapper;

    private List<User> userList;


    @BeforeEach
    public void userTest() {

        this.userList = new ArrayList<>();

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstsName("Jonny");
        user1.setMiddleName("Mauricio");
        user1.setLastName("Canabal");
        user1.setSecondLastName("Ospina");
        user1.setPhoneNumber("+57 3185984575");
        user1.setEmail("jmco@gmail.com");
        user1.setUsername("jonny");
        user1.setPassword("12345");
        user1.setEnabled(true);
        user1.setAdmin(false);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstsName("Karen");
        user2.setMiddleName("");
        user2.setLastName("Rubiano");
        user2.setSecondLastName("Torres");
        user2.setPhoneNumber("+57 3185984575");
        user2.setEmail("krn2@gmail.com");
        user2.setUsername("karen");
        user2.setPassword("12345");
        user1.setEnabled(true);
        user1.setAdmin(true);

        userList.add(user1);
        userList.add(user2);
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findAllTest() throws Exception {

        given(userService.findAllByOrderById()).willReturn(userList);

        ResultActions response = mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userList)));

        for (int i = 0; i < userList.size(); i++) {
            User currentUser = userList.get(i);
            response.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[" + i + "].id").value(currentUser.getId()))
                    .andExpect(jsonPath("$[" + i + "].firstsName").value(currentUser.getFirstsName()))
                    .andExpect(jsonPath("$[" + i + "].middleName").value(currentUser.getMiddleName()))
                    .andExpect(jsonPath("$[" + i + "].lastName").value(currentUser.getLastName()))
                    .andExpect(jsonPath("$[" + i + "].secondLastName").value(currentUser.getSecondLastName()))
                    .andExpect(jsonPath("$[" + i + "].phoneNumber").value(currentUser.getPhoneNumber()))
                    .andExpect(jsonPath("$[" + i + "].email").value(currentUser.getEmail()))
                    .andExpect(jsonPath("$[" + i + "].username").value(currentUser.getUsername()));
        }
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findByIdTest() throws Exception {

        Long userToFindId = 1L;
        User userToFind = new User();
        for (int i = 0; i < userList.size(); i++) {
            User userToFor = userList.get(i);
            if (userToFor.getId().equals(userToFindId)) {
                userToFind = userToFor;
                break;
            }
        }

        given(userService.findById(anyLong())).willReturn(Optional.of(userToFind));

        ResultActions response = mockMvc.perform(get("/user/{id}", userToFindId)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(userToFind)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userToFind.getId()));

    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void saveUserTest() throws Exception {

        User user = new User();

        user.setId(3L);
        user.setFirstsName("Diego");
        user.setMiddleName("");
        user.setLastName("Briñez");
        user.setSecondLastName("");
        user.setPhoneNumber("+57 3183695874");
        user.setEmail("diego.briñez@gmail.com");
        user.setUsername("diego");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setAdmin(false);

        given(userService.save(any(User.class))).willReturn(user);

        System.out.println("Body Content: " + objectMapper.writeValueAsString(user));

        ResultActions response = mockMvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.firstsName").value(user.getFirstsName()))
                .andExpect(jsonPath("$.middleName").value(user.getMiddleName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.secondLastName").value(user.getSecondLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void saveAdminTest() throws Exception {

        User user = new User();

        user.setId(4L);
        user.setFirstsName("Diego");
        user.setMiddleName("");
        user.setLastName("Briñez");
        user.setSecondLastName("");
        user.setPhoneNumber("+57 3183695874");
        user.setEmail("diego.briñez@gmail.com");
        user.setUsername("diego");
        user.setPassword("12345");
        user.setEnabled(true);
        user.setAdmin(true);

        given(userService.save(any(User.class))).willReturn(user);

        ResultActions response = mockMvc.perform(post("/user/create/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.firstsName").value(user.getFirstsName()))
                .andExpect(jsonPath("$.middleName").value(user.getMiddleName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.secondLastName").value(user.getSecondLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void updateUserTest() throws Exception {

        Long userId = 1L;
        User currentUser = new User();
        for (int i = 0; i < userList.size(); i++) {
            User userToFor = userList.get(i);
            if (userToFor.getId().equals(userId)) {
                currentUser = userToFor;
                break;
            }
        }

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstsName("Diego");
        updatedUser.setMiddleName("");
        updatedUser.setLastName("Briñez");
        updatedUser.setSecondLastName("");
        updatedUser.setPhoneNumber("+57 3183695874");
        updatedUser.setEmail("diego.briñez@gmail.com");
        updatedUser.setUsername("diego");
        updatedUser.setPassword("12345");
        updatedUser.setEnabled(true);
        updatedUser.setAdmin(false);

        for (int i = 0; i < userList.size(); i++) {
            System.out.println("User Before To Update - FirstsName: " + userList.get(i).getFirstsName());
            System.out.println("User Before To Update - LastName: " + userList.get(i).getLastName());
            System.out.println("User Before To Update - Email: " + userList.get(i).getEmail());
            System.out.println("User Before To Update - Username: " + userList.get(i).getUsername());
            System.out.println("-----------------------------------------------------------");
        }
        System.out.println("****************************************************************");

        given(userService.findById(anyLong())).willReturn(Optional.of(currentUser));

        given(userService.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/user/update/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));

        for (int i = 0; i < userList.size(); i++) {
            System.out.println("User After To Update - FirstsName: " + userList.get(i).getFirstsName());
            System.out.println("User After To Update - LastName: " + userList.get(i).getLastName());
            System.out.println("User After To Update - Email: " + userList.get(i).getEmail());
            System.out.println("User After To Update - Username: " + userList.get(i).getUsername());
            System.out.println("-----------------------------------------------------------");
        }

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.firstsName").value(updatedUser.getFirstsName()))
                .andExpect(jsonPath("$.middleName").value(updatedUser.getMiddleName()))
                .andExpect(jsonPath("$.lastName").value(updatedUser.getLastName()))
                .andExpect(jsonPath("$.secondLastName").value(updatedUser.getSecondLastName()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedUser.getPhoneNumber()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.username").value(updatedUser.getUsername()))
                .andExpect(jsonPath("$.password").value(updatedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void deleteUser() throws Exception {
        User currentUser = userList.get(0);
        Long userToDeleteId = currentUser.getId();

        given(userService.findById(userToDeleteId)).willReturn(Optional.of(currentUser));

        doNothing().when(userService).deleteById(userToDeleteId);

        MvcResult result = mockMvc.perform(delete("/user/delete/{id}", userToDeleteId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + content);

        verify(userService, times(1)).deleteById(userToDeleteId);
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findUserTest() throws Exception {
        String firstsName = "Jonny";
        User userToFind = new User();

        for (int i = 0; i < userList.size(); i++) {
            User userToFor = userList.get(i);
            if (userToFor.getFirstsName().equals(firstsName)) {
                userToFind = userToFor;
                break;
            }
        }

        List<User> userList2 = Arrays.asList(userToFind);

        given(userService.findByNamesIgnoreCase(anyString(), anyString(), anyString(), anyString())).willReturn(userList2);

        ResultActions response = mockMvc.perform(post("/user/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstsName\":\"" + firstsName + "\"}"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstsName").value(firstsName));
    }
}

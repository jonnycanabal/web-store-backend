package com.web.store.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.store.entity.Role;
import com.web.store.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @InjectMocks
    private ObjectMapper objectMapper;

    private final List<Role> roleList = new ArrayList<>();

    @BeforeEach
    public void testRole() {

//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(new UsernamePasswordAuthenticationToken("username", "password"));

        Role role1 = new Role();
        role1.setId(1L);
        role1.setRoleName("ROLE_USER");


        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("ROLE_ADMIN");

        roleList.add(role1);
        roleList.add(role2);

    }

    @Test
    // Simulates an authenticated user with the role USER
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void findAllTest() throws Exception {

        given(roleService.findAll()).willReturn(roleList);

        ResultActions response = mockMvc.perform(get("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleList)));

        for (int i = 0; i < roleList.size(); i++) {
            Role roleToFor = roleList.get(i);
            response.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[" + i + "].id").value(roleToFor.getId()))
                    .andExpect(jsonPath("$[" + i + "].roleName").value(roleToFor.getRoleName()));
        }
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void findByIdTest() throws Exception {

        Long roleToFindId = 1L;
        Role currentRole = new Role();


        for (int i = 0; i < roleList.size(); i++) {
            Role roleToFor = roleList.get(i);
            if (roleToFor.getId().equals(roleToFindId)) {
                currentRole = roleToFor;
                break;
            }
        }

        given(roleService.findById(anyLong())).willReturn(Optional.of(currentRole));

        ResultActions response = mockMvc.perform(get("/role/{id}", roleToFindId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currentRole)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(currentRole.getId().intValue()))
                .andExpect(jsonPath("$.roleName").value(currentRole.getRoleName()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void saveRolTest() throws Exception {
        Role roleToSave = new Role();

        roleToSave.setId(3L);
        roleToSave.setRoleName("ROLE_TEST");

        given(roleService.save(any(Role.class))).willReturn(roleToSave);

        ResultActions response = mockMvc.perform(post("/role/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleToSave)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(roleToSave.getId()))
                .andExpect(jsonPath("$.roleName").value(roleToSave.getRoleName()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void updatedRoleTest() throws Exception {
        Long roleToFindId = 1L;
        Role currentRole = new Role();

        for (int i = 0; i < roleList.size(); i++) {
            Role roleToFor = roleList.get(i);
            if (roleToFor.getId().equals(roleToFindId)) {
                currentRole = roleToFor;
            }
        }

        Role updatedRole = new Role();
        updatedRole.setId(currentRole.getId());
        updatedRole.setRoleName("ROLE_UPDATED");

        for (int i = 0; i < roleList.size(); i++) {
            System.out.println("Role before to Update: " + roleList.get(i).getRoleName());
        }

        given(roleService.findById(anyLong())).willReturn(Optional.of(currentRole));

        given(roleService.save(any(Role.class))).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/role/update/{id}", currentRole.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRole)));

        for (int i = 0; i < roleList.size(); i++) {
            System.out.println("Role After del Edit: " + roleList.get(i).getRoleName());
        }

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(updatedRole.getId()))
                .andExpect(jsonPath("$.roleName").value(updatedRole.getRoleName()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void deleteRoleTest() throws Exception {

        Role currentRole = roleList.get(0);
        Long roleToDeleteId = currentRole.getId();

        given(roleService.findById(roleToDeleteId)).willReturn(Optional.of(currentRole));

        doNothing().when(roleService).deleteById(roleToDeleteId);

        MvcResult result = mockMvc.perform(delete("/role/delete/{id}", roleToDeleteId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + content);

        verify(roleService, times(1)).deleteById(roleToDeleteId);
    }
}

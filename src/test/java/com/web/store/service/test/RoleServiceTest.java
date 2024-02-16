package com.web.store.service.test;

import com.web.store.entity.Role;
import com.web.store.repository.RoleRepository;
import com.web.store.services.RoleServiceImplement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImplement roleServiceImplement;

    private final List<Role> roleList = new ArrayList<>();

    @BeforeEach
    public void testRole() {

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
    public void testFindAll() {

        Mockito.when(roleRepository.findAll()).thenReturn(roleList);

        Iterable<Role> currentRole = roleServiceImplement.findAll();

        for (int i = 0; i < roleList.size(); i++) {
            System.out.println(roleList.get(i).getRoleName());
        }

        Assertions.assertEquals(roleList, currentRole);
    }

    @Test
    public void testFindById() {

        Role currentRole = new Role();
        Long roleToFindId = 1L;

        for (int i = 0; i < roleList.size(); i++) {
            Role roleToFor = roleList.get(i);
            if (roleToFor.getId().equals(roleToFindId)) {
                currentRole = roleToFor;
            }
        }

        Mockito.when(roleRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(currentRole));

        Optional<Role> result = roleServiceImplement.findById(1L);

        System.out.println(result.get().getId() + " " + result.get().getRoleName());
        System.out.println(currentRole.getId() + " " + currentRole.getRoleName());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(currentRole.getId(), roleToFindId);
    }

    @Test
    public void roleSaveTest() {

        Role roleToSave = new Role();

        roleToSave.setId(3L);
        roleToSave.setRoleName("ROLE_TEST");

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(roleToSave);

        Role result = roleServiceImplement.save(roleToSave);

        System.out.println("Repository: " + result.getRoleName());
        System.out.println("Result: " + roleToSave.getRoleName());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, roleToSave);
    }

    @Test
    public void roleDeleteTest() {
        Role role = roleList.get(0);
        Long roleToDeleteId = role.getId();

        roleServiceImplement.deleteById(roleToDeleteId);

        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(roleToDeleteId);

        System.out.println("Role deleted is ID: " + role.getId() + " - Name: " + role.getRoleName());
    }
}

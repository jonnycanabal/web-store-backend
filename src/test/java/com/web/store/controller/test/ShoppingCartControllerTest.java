package com.web.store.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.store.entity.Product;
import com.web.store.entity.ShoppingCart;
import com.web.store.entity.User;
import com.web.store.services.ShoppingCartService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private ObjectMapper objectMapper;

    private List<ShoppingCart> shoppingCartList;


    @BeforeEach
    public void productTest() {

        this.shoppingCartList = new ArrayList<>();

        ShoppingCart shoppingCart1 = new ShoppingCart();
        shoppingCart1.setId(1L);

        ShoppingCart shoppingCart2 = new ShoppingCart();
        shoppingCart2.setId(2L);

        shoppingCartList.add(shoppingCart1);
        shoppingCartList.add(shoppingCart2);
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void finAllTest() throws Exception {

        given(shoppingCartService.findAll()).willReturn(shoppingCartList);

        ResultActions response = mockMvc.perform(get("/shoppingCart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shoppingCartList)));

        for (int i = 0; i < shoppingCartList.size(); i++) {
            ShoppingCart curretShoppingCart = shoppingCartList.get(i);
            response.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[" + i + "].id").value(curretShoppingCart.getId()));
        }
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findByIdTest() throws Exception {

        Long shoppingCartToFindId = 1L;
        ShoppingCart currentShoppingCart = new ShoppingCart();
        for (int i = 0; i < shoppingCartList.size(); i++) {
            ShoppingCart shoppingCartToFor = shoppingCartList.get(i);
            if (shoppingCartToFor.getId().equals(shoppingCartToFindId)) {
                currentShoppingCart = shoppingCartToFor;
                break;
            }
        }

        given(shoppingCartService.findById(anyLong())).willReturn(Optional.of(currentShoppingCart));

        ResultActions response = mockMvc.perform(get("/shoppingCart/{id}", shoppingCartToFindId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currentShoppingCart)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(currentShoppingCart.getId()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void saveShoppingCartTest() throws Exception{

        User user = new User();
        user.setId(1L);
        user.setFirstsName("Jonny");
        user.setMiddleName("Mauricio");
        user.setLastName("Canabal");
        user.setSecondLastName("Ospina");
        user.setPhoneNumber("+57 3185984575");
        user.setEmail("jmco@gmail.com");
        user.setUsername("jonny");
        user.setPassword("12345");

        Product product = new Product();
        product.setId(1L);
        product.setProductName("SuperStar");
        product.setPrice(250000);
        product.setPhoto("Photo Product".getBytes());

        ShoppingCart shoppingCartToSave = new ShoppingCart();
        shoppingCartToSave.setId(3L);
        shoppingCartToSave.setUser(user);
        shoppingCartToSave.getProducts().add(product);

        given(shoppingCartService.save(any(ShoppingCart.class))).willReturn(shoppingCartToSave);

        ResultActions response = mockMvc.perform(post("/shoppingCart/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shoppingCartToSave)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(shoppingCartToSave.getId()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void updateShoppingCartTest() throws Exception {
        Long shoppingCartToEditId = 1L;

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

        User user2 = new User();
        user2.setId(1L);
        user2.setFirstsName("Karen");
        user2.setMiddleName("");
        user2.setLastName("Rubiano");
        user2.setSecondLastName("Torres");
        user2.setPhoneNumber("+57 3185984575");
        user2.setEmail("krn2@gmail.com");
        user2.setUsername("karen");
        user2.setPassword("12345");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("SuperStar");
        product1.setPrice(250000);
        product1.setPhoto("Photo Product".getBytes());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Adizero");
        product2.setPrice(100000);
        product2.setPhoto("Photo Product".getBytes());

        this.shoppingCartList = new ArrayList<>();

        ShoppingCart shoppingCart1 = new ShoppingCart();
        shoppingCart1.setId(1L);

        ShoppingCart shoppingCart2 = new ShoppingCart();
        shoppingCart2.setId(2L);

        shoppingCart1.setUser(user1);
        shoppingCart1.getProducts().add(product1);

        shoppingCart2.setUser(user2);
        shoppingCart2.getProducts().add(product2);

        shoppingCartList.add(shoppingCart1);
        shoppingCartList.add(shoppingCart2);

        ShoppingCart currentShoppingCart = new ShoppingCart();

        for(int i = 0; i < shoppingCartList.size(); i++){
            ShoppingCart shoppingCartToFor = shoppingCartList.get(i);
            if (shoppingCartToFor.getId().equals(shoppingCartToEditId)){
                currentShoppingCart = shoppingCartToFor;
            }
        }

        ShoppingCart updatedShoppingCart = new ShoppingCart();
        updatedShoppingCart.setId(currentShoppingCart.getId());
        updatedShoppingCart.getProducts().add(product1);
        updatedShoppingCart.getProducts().add(product2);
        updatedShoppingCart.setUser(currentShoppingCart.getUser());

        for (int i = 0; i < shoppingCartList.size(); i++){
            System.out.println("Before to update: " + shoppingCartList.get(i).getId());
            System.out.println("Before to update: " + shoppingCartList.get(i).getUser());
            System.out.println("Before to update: " + shoppingCartList.get(i).getProducts());
        }

        given(shoppingCartService.findById(anyLong())).willReturn(Optional.of(currentShoppingCart));

        given(shoppingCartService.save(any(ShoppingCart.class))).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/shoppingCart/update/{id}", shoppingCartToEditId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedShoppingCart)));

        for (int i = 0; i < shoppingCartList.size(); i++){
            System.out.println("After to update: " + shoppingCartList.get(i).getId());
            System.out.println("After to update: " + shoppingCartList.get(i).getUser());
            System.out.println("After to update: " + shoppingCartList.get(i).getProducts());
        }

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(updatedShoppingCart.getId()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void deleteTest() throws Exception {
        ShoppingCart currentShoppingCart = shoppingCartList.get(0);
        Long shoppingCartToDeleteId = currentShoppingCart.getId();

        given(shoppingCartService.findById(shoppingCartToDeleteId)).willReturn(Optional.of(currentShoppingCart));

        doNothing().when(shoppingCartService).deleteById(shoppingCartToDeleteId);

        MvcResult result = mockMvc.perform(delete("/shoppingCart/delete/{id}", shoppingCartToDeleteId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + content);

        verify(shoppingCartService, times(1)).deleteById(shoppingCartToDeleteId);
    }
}

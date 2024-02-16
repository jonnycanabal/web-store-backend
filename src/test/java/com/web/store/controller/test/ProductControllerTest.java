package com.web.store.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.store.entity.Product;
import com.web.store.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Product> productList;

    @BeforeEach
    public void productTest() {

        this.productList = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("SuperStar");
        product1.setPrice(200000);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Adizero");
        product2.setPrice(250000);

        productList.add(product1);
        productList.add(product2);
    }


    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findAllTest() throws Exception {

        given(productService.findAllByOrderById()).willReturn(productList);

        ResultActions response = mockMvc.perform(get("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productList)));

        for (int i = 0; i < productList.size(); i++) {
            Product currentProduct = productList.get(i);
            response.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(productList.size()))
                    .andExpect(jsonPath("$[" + i + "].id").value(currentProduct.getId().intValue()))
                    .andExpect(jsonPath("$[" + i + "].productName").value(currentProduct.getProductName()))
                    .andExpect(jsonPath("$[" + i + "].price").value(currentProduct.getPrice()));
        }
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findByIdTest() throws Exception {

        Long productId = 1L;
        Product productToFind = null;
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                productToFind = product;
                break;
            }
        }
        given(productService.findById(anyLong())).willReturn(Optional.ofNullable(productToFind));

        ResultActions response = mockMvc.perform(get("/product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productList)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productToFind.getId()))
                .andExpect(jsonPath("$.productName").value(productToFind.getProductName()))
                .andExpect(jsonPath("$.price").value(productToFind.getPrice()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void saveProductTest() throws Exception {

        Product product = new Product();
        product.setId(3L);
        product.setProductName("For One");
        product.setPrice(450000);

        given(productService.save(any(Product.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/product/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.productName", is(product.getProductName())))
                .andExpect(jsonPath("$.price", is(product.getPrice())));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void updateProductTest() throws Exception {

        Long productId = 1L;
        Product productToFind = null;

        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                productToFind = product;
                break;
            }
        }

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setProductName("For One");
        updatedProduct.setPrice(450000);

        given(productService.findById(anyLong())).willReturn(Optional.ofNullable(productToFind));

        assert productToFind != null;
        System.out.println("Product before to update: " + productToFind.getProductName());

        given(productService.save(any(Product.class))).willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/product/update/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)));

        System.out.println("Product after to update: " + updatedProduct.getProductName());

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(updatedProduct.getId()))
                .andExpect(jsonPath("$.productName").value(updatedProduct.getProductName()))
                .andExpect(jsonPath("$.price").value(updatedProduct.getPrice()));
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN"})
    public void deleteProductTest() throws Exception {

        Product currentProduct = productList.get(0);
        Long productToDeleteId = currentProduct.getId();

        given(productService.findById(productToDeleteId)).willReturn(Optional.of(currentProduct));

        doNothing().when(productService).deleteById(productToDeleteId);

        MvcResult result = mockMvc.perform(delete("/product/delete/{id}", productToDeleteId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + content);

        verify(productService, times(1)).deleteById(productToDeleteId);
    }

    @Test
    @WithMockUser(username = "Admin",password = "12345", roles = {"ADMIN","USER"})
    public void findProductTest() throws Exception {

        String productName = "SuperStar";
        Product productToFind = new Product();

        for (int i = 0; i < productList.size(); i++) {
            Product productToFor = productList.get(i);
            if (productToFor.getProductName().equals(productName)) {
                productToFind = productToFor;
                break;
            }
        }

        List<Product> productList = Arrays.asList(productToFind);

        given(productService.findByProductNameIgnoreCase(anyString())).willReturn(productList);


        ResultActions response = mockMvc.perform(post("/product/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productName\":\"" + productName + "\"}"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value(productName));
    }
}

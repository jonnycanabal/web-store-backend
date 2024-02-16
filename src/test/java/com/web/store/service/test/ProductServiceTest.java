package com.web.store.service.test;

import com.web.store.entity.Product;
import com.web.store.repository.ProductRepository;
import com.web.store.services.ProductServiceImplement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImplement productServiceImplement;

    private final List<Product> productList = new ArrayList<>();

    @BeforeEach
    public void testProduct() {


        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Superstar");
        product1.setPrice(150000);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Adizero");
        product2.setPrice(300000);

        productList.add(product1);
        productList.add(product2);

    }

    @Test
    public void tFinAllTest() {

        Mockito.when(productRepository.findAll()).thenReturn(productList);

        Iterable<Product> result = productServiceImplement.findAll();

        for (int i = 0; i < productList.size(); i++) {
            System.out.println(productList.get(i).getId() + ", "
                    + productList.get(i).getProductName() + ", "
                    + productList.get(i).getPrice());
        }

        Assertions.assertEquals(productList, result);
    }

    @Test
    public void FinByIdTest() {

        Product currentProduct = new Product();
        Long productToFindId = 1L;

        for (int i = 0; i < productList.size(); i++) {
            Product productToFor = productList.get(i);
            if (productToFor.getId().equals(productToFindId)) {
                currentProduct = productToFor;
            }
        }

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(currentProduct));

        Optional<Product> response = productServiceImplement.findById(1L);

        System.out.println(response.get().getId() + " " + response.get().getProductName());
        System.out.println(currentProduct.getId() + " " + currentProduct.getProductName());

        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(currentProduct.getId(), productToFindId);
    }

    @Test
    public void SaveProductTest() {

        Product productToSave = new Product();

        productToSave.setId(3L);
        productToSave.setProductName("For One");
        productToSave.setPrice(450000);

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(productToSave);

        Product result = productServiceImplement.save(productToSave);

        System.out.println("Repository - new product name: " + result.getProductName());
        System.out.println("Result - new product name: " + productToSave.getProductName());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, productToSave);
    }

    @Test
    public void productDeleteTest() {

        Product product = productList.get(0);
        Long productToDelete = product.getId();

        productServiceImplement.deleteById(productToDelete);

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(productToDelete);

        System.out.println("Product deleted is ID: " + product.getId() + " - Name: " + product.getProductName());

    }

    @Test
    public void testFindByProductName() {

        Product currentProduct = null;

        String productToFind = "Superstar";

        for (Product productToFor : productList) {
            if (productToFor.getProductName().equals(productToFind)) {
                currentProduct = productToFor;
            }
        }

        Mockito.when(productRepository.findByProductNameIgnoreCase(
                Mockito.anyString())).thenReturn(Arrays.asList(currentProduct));

        List<Product> response = productServiceImplement.findByProductNameIgnoreCase("SuperStar");

        Product retrivedProduct = response.get(0);

        System.out.println(response.get(0).getProductName());
        System.out.println(retrivedProduct.getProductName());

        Assertions.assertNotNull(response);
        assertEquals(1, response.size());
        assert currentProduct != null;
        assertEquals(currentProduct.getProductName(), retrivedProduct.getProductName());
    }
}

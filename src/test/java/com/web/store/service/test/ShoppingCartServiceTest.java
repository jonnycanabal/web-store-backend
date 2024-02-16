package com.web.store.service.test;

import com.web.store.entity.ShoppingCart;
import com.web.store.repository.ShoppingCartRepository;
import com.web.store.services.ShoppingCartServiceImplement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private ShoppingCartServiceImplement shoppingCartServiceImplement;

    private ShoppingCart shoppingCart;
    private final List<ShoppingCart> shoppingCartList = new ArrayList<>();

    @BeforeEach
    public void shoppingCartTest() {

        ShoppingCart shoppingCart1 = new ShoppingCart();
        shoppingCart1.setId(1L);

        ShoppingCart shoppingCart2 = new ShoppingCart();
        shoppingCart2.setId(2L);

        shoppingCartList.add(shoppingCart1);
        shoppingCartList.add(shoppingCart2);

    }

    @Test
    public void findAllTest() {

        Mockito.when(shoppingCartRepository.findAll()).thenReturn(shoppingCartList);

        Iterable<ShoppingCart> result = shoppingCartServiceImplement.findAll();

        for (int i = 0; i < shoppingCartList.size(); i++) {
            System.out.println("Id: " + shoppingCartList.get(i).getId() + ", " +
                    "User: " + shoppingCartList.get(i).getUser() + ", " +
                    "Products: " + shoppingCartList.get(i).getProducts() + ", " +
                    "Items: " + shoppingCartList.get(i).getItems());
        }
        assertEquals(shoppingCartList, result);
    }

    @Test
    public void findByIdTest() {

        ShoppingCart currentShoppingCart = new ShoppingCart();
        Long shoppingCartToFindId = 1L;

        for (int i=0; i<shoppingCartList.size(); i++){
            ShoppingCart shoppingCartToFor = shoppingCartList.get(i);
            if (shoppingCartToFor.getId().equals(shoppingCartToFindId)){
                currentShoppingCart = shoppingCartToFor;
            }
        }

        Mockito.when(shoppingCartRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(currentShoppingCart));

        Optional<ShoppingCart> response = shoppingCartServiceImplement.findById(shoppingCartToFindId);

        System.out.println("Id: " + response.get().getId() + ", " +
                "User: " + response.get().getUser()+ ", " +
                "Products: " + response.get().getProducts() + ", " +
                "Items: " + response.get().getItems());

        Assertions.assertTrue(response.isPresent());
        assertEquals(currentShoppingCart.getId(), response.get().getId());
    }

    @Test
    public void saveShoppingCartTest() {

        ShoppingCart shoppingCartToSave = new ShoppingCart();

        shoppingCartToSave.setId(3L);
        shoppingCartToSave.setUser(null);
        shoppingCartToSave.setProducts(null);
        shoppingCartToSave.setItems(null);

        Mockito.when(shoppingCartRepository.save(Mockito.any(ShoppingCart.class))).thenReturn(shoppingCartToSave);

        ShoppingCart result = shoppingCartServiceImplement.save(shoppingCartToSave);

        System.out.println("Id: " + result.getId()+ ", User:  " + result.getUser()
        + ", Products: " + result.getProducts() + ", Items: " + result.getItems());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(shoppingCartToSave, result);

    }

    @Test
    public void testDeleteShoppingCart() {

        ShoppingCart shoppingCart = shoppingCartList.get(0);
        Long shoppingCartToDelete = shoppingCart.getId();

        shoppingCartServiceImplement.deleteById(shoppingCartToDelete);

        Mockito.verify(shoppingCartRepository, Mockito.times(1)).deleteById(shoppingCartToDelete);

        System.out.println("Shopping Cart deleted is Id: " + shoppingCart.getId()+ ", User:  " + shoppingCart.getUser()
                + ", Products: " + shoppingCart.getProducts() + ", Items: " + shoppingCart.getItems());
    }
}

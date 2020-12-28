package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.inject(cartController, "userRepository", userRepo);
        TestUtils.inject(cartController, "cartRepository", cartRepo);
        TestUtils.inject(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void testAddToCart() {
        when(userRepo.findByUsername("testuser")).thenReturn(getMockUser());
        when(itemRepo.findById(0L)).thenReturn(Optional.of(getMockItem()));

        ModifyCartRequest cartRequest = getRequest();
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(getMockUser().getUsername(), cart.getUser().getUsername());
        assertEquals(1, cart.getItems().size());
        assertEquals(getMockItem().getName(), cart.getItems().get(0).getName());

        ModifyCartRequest invalidRequest = new ModifyCartRequest();
        invalidRequest.setUsername("test");
        invalidRequest.setQuantity(1);
        invalidRequest.setItemId(0);
        response = cartController.addTocart(invalidRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        invalidRequest.setUsername("testuser");
        invalidRequest.setItemId(1);
        response = cartController.addTocart(invalidRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void testRemoveFromCart() {
        User mockuser = getMockUser();
        mockuser.getCart().addItem(getMockItem());

        when(userRepo.findByUsername("testuser")).thenReturn(mockuser);
        when(itemRepo.findById(0L)).thenReturn(Optional.of(getMockItem()));

        ResponseEntity<Cart> response = cartController.removeFromcart(getRequest());
        Cart cart = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, cart.getItems().size());

        ModifyCartRequest request = getRequest();
        request.setItemId(1L);
        response = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private ModifyCartRequest getRequest() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setUsername("testuser");
        return request;
    }

    private User getMockUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("testpassword");

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        return user;
    }

    private Item getMockItem() {
        Item item = new Item();
        item.setName("Notebook");
        item.setId(0L);
        item.setPrice(new BigDecimal(1000));
        item.setDescription("Best notebook ever");
        return item;
    }

}

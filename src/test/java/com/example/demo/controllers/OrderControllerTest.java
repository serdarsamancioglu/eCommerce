package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.inject(orderController, "userRepository", userRepo);
        TestUtils.inject(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void testSubmitOrder() {
        when(userRepo.findByUsername("testuser")).thenReturn(getMockUser());
        ResponseEntity<UserOrder> response = orderController.submit("testuser");
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(new BigDecimal(1000), order.getTotal());
        assertEquals(1, order.getItems().size());
        assertEquals("Notebook", order.getItems().get(0).getName());
    }

    @Test
    public void testGetOrdersForUser() {
        when(userRepo.findByUsername("testuser")).thenReturn(getMockUser());
        when(orderRepo.findByUser(any())).thenReturn(getMockOrders());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");
        List<UserOrder> orders = response.getBody();
        UserOrder mockOrder = getMockOrders().get(0);

        assertEquals(mockOrder.getTotal(), orders.get(0).getTotal());
        assertEquals(mockOrder.getUser().getUsername(), orders.get(0).getUser().getUsername());
        assertEquals(mockOrder.getItems().get(0).getName(), orders.get(0).getItems().get(0).getName());

        response = orderController.getOrdersForUser("test");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User getMockUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("testpassword");

        Item item = new Item();
        item.setName("Notebook");
        item.setPrice(new BigDecimal(1000));
        item.setDescription("Best notebook ever");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(Arrays.asList(item));
        cart.setTotal(new BigDecimal(1000));
        user.setCart(cart);
        return user;
    }

    private List<UserOrder> getMockOrders() {
        return Arrays.asList(UserOrder.createFromCart(getMockUser().getCart()));
    }
}

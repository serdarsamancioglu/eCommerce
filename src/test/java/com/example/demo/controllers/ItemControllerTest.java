package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.inject(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void testGetItems() {
        when(itemRepo.findAll()).thenReturn(Arrays.asList(getMockItem()));

        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> items = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item mockItem = getMockItem();
        assertEquals(mockItem.getName(), items.get(0).getName());
        assertEquals(mockItem.getPrice(), items.get(0).getPrice());
        assertEquals(mockItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    public void testGetItemById() {
        when(itemRepo.findById(0L)).thenReturn(Optional.of(getMockItem()));

        ResponseEntity<Item> response = itemController.getItemById(0L);
        Item item = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(getMockItem().getPrice(), item.getPrice());
        assertEquals(getMockItem().getName(), item.getName());
        assertEquals(getMockItem().getDescription(), item.getDescription());

        ResponseEntity<Item> response2 = itemController.getItemById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    public void testGetItemsByName() {
        when(itemRepo.findByName("Notebook")).thenReturn(Arrays.asList(getMockItem()));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Notebook");
        List<Item> items = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(getMockItem().getPrice(), items.get(0).getPrice());
        assertEquals(getMockItem().getName(), items.get(0).getName());
        assertEquals(getMockItem().getDescription(), items.get(0).getDescription());

        ResponseEntity<List<Item>> response2 = itemController.getItemsByName("Phone");
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }



    private Item getMockItem() {
        Item item = new Item();
        item.setName("Notebook");
        item.setPrice(new BigDecimal(1000));
        item.setDescription("Best notebook ever");
        return item;
    }
}

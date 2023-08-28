package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {
    @Test
    public void contextLoads() {
    }

    public static final String ITEM_TO_ADD = "Widget";
    private UserController userController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private CartController cartController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private ItemController itemController;
    private OrderController orderController;
    private final OrderRepository orderRepo = mock(OrderRepository.class);

    private static CreateUserRequest getUserRequest() {
        CreateUserRequest ur = new CreateUserRequest();
        ur.setUsername("test");
        ur.setPassword("test");
        ur.setConfirmPassword("test");
        return ur;
    }

    private static void extracted(ResponseEntity<User> response) {
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    private static CreateUserRequest getCreateUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        return createUserRequest;
    }

    private ResponseEntity<User> getUserResponseEntity(CreateUserRequest createUserRequest) {
        return userController.createUser(createUserRequest);
    }

    private void extracted() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
    }

    private static ModifyCartRequest getModifyCartRequest() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(0L);
        request.setQuantity(1);
        request.setUsername("test");
        return request;
    }

    private static createUserAddCart getCreateUserAddCart() {
        User user = new User();
        user.setUsername("test");
        Item item = createFirstItem();
        Cart cart = new Cart();
        cart.setId(0L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        user.setCart(cart);
        return new createUserAddCart(user, item);
    }

    private static class createUserAddCart {
        public final User user;
        public final Item item;

        public createUserAddCart(User user, Item item) {
            this.user = user;
            this.item = item;
        }
    }

    private static userItemCart getUserItemCart() {
        User user = new User();
        user.setUsername("test");
        Item item = createFirstItem();
        Cart cart = new Cart();
        cart.setId(0L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        user.setCart(cart);
        return new userItemCart(user, item);
    }

    private static class userItemCart {
        public final User user;
        public final Item item;

        public userItemCart(User user, Item item) {
            this.user = user;
            this.item = item;
        }
    }

    private static Cart getCart(Item item, Result result) {
        Cart cart = new Cart();
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setId(0L);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(result.user);
        return cart;
    }

    private static Result getResult() {
        String username = "test";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setId(0L);
        return new Result(username, user);
    }

    private static class Result {
        public final String username;
        public final User user;

        public Result(String username, User user) {
            this.username = username;
            this.user = user;
        }
    }

    public static Item createSecondItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Square Widget");
        item.setPrice(new BigDecimal("1.99"));
        item.setDescription("A widget that is square");
        return item;
    }

    public static Item createFirstItem() {
        Item item = new Item();
        item.setId(0L);
        item.setName(ITEM_TO_ADD);
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");
        return item;
    }

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        orderController = new OrderController();
        com.example.demo.TestUtils.injectObjects(orderController, "userRepository", userRepo);
        com.example.demo.TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void testUserCreationFail() {
        when(encoder.encode("test")).thenReturn("thisIsHashed");
        CreateUserRequest ur = getUserRequest();
        final ResponseEntity<User> response = getUserResponseEntity(ur);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void createUserHappyPath() {
        extracted();
        CreateUserRequest createUserRequest = getCreateUserRequest();
        final ResponseEntity<User> response = getUserResponseEntity(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        extracted(response);
    }

    @Test
    public void findByUserName() {
        extracted();
        CreateUserRequest createUserRequest = getCreateUserRequest();
        final ResponseEntity<User> response = getUserResponseEntity(createUserRequest);
        User user = response.getBody();
        when(userRepo.findByUsername("test")).thenReturn(user);
        final ResponseEntity<User> userResponseEntity = userController.findByUserName("test");
        extracted(userResponseEntity);
    }

    @Test
    public void findById() {
        extracted();
        CreateUserRequest createUserRequest = getCreateUserRequest();
        final ResponseEntity<User> response = getUserResponseEntity(createUserRequest);
        User user = response.getBody();
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.ofNullable(user));
        final ResponseEntity<User> userResponseEntity = userController.findById(0L);
        extracted(userResponseEntity);
    }

    @Test
    public void addToCart() {
        createUserAddCart result = getCreateUserAddCart();
        when(userRepo.findByUsername("test")).thenReturn(result.user);
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(result.item));
        ModifyCartRequest request = getModifyCartRequest();
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart retrievedCart = response.getBody();
        assertNotNull(retrievedCart);
        assertEquals(0L, (long) retrievedCart.getId());
        List<Item> items = retrievedCart.getItems();
        assertNotNull(items);
        Item retrievedItem = items.get(0);
        assertEquals(2, items.size());
        assertNotNull(retrievedItem);
        assertEquals(result.item, retrievedItem);
        assertEquals(new BigDecimal("5.98"), retrievedCart.getTotal());
        assertEquals(result.user, retrievedCart.getUser());
    }

    @Test
    public void testAddToCartNullUser() {
        createUserAddCart result = getCreateUserAddCart();
        when(userRepo.findByUsername("test")).thenReturn(null);
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(result.item));
        ModifyCartRequest request = getModifyCartRequest();
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() {
        userItemCart result = getUserItemCart();
        when(userRepo.findByUsername("test")).thenReturn(result.user);
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(result.item));
        ModifyCartRequest request = getModifyCartRequest();
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart retrievedCart = response.getBody();
        assertNotNull(retrievedCart);
        assertEquals(0L, (long) retrievedCart.getId());
        List<Item> items = retrievedCart.getItems();
        assertNotNull(items);
        assertEquals(0, items.size());
        assertEquals(new BigDecimal("0.00"), retrievedCart.getTotal());
        assertEquals(result.user, retrievedCart.getUser());
    }

    @Test
    public void testGetItems() {
        Item item0 = createFirstItem();
        Item item1 = createSecondItem();
        List<Item> items = new ArrayList<>(2);
        items.add(item0);
        items.add(item1);
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> retrievedItems = response.getBody();
        assertNotNull(retrievedItems);
        assertEquals(2, retrievedItems.size());
        assertEquals(item0, retrievedItems.get(0));
        assertEquals(item1, retrievedItems.get(1));
    }

    @Test
    public void testGetItemById() {
        Item item0 = createFirstItem();
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item0));
        ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item retrievedItem = response.getBody();
        assertEquals(item0, retrievedItem);
        assertNotNull(retrievedItem);
        assertEquals(item0.getName(), retrievedItem.getName());
        assertEquals(item0.getId(), retrievedItem.getId());
        assertEquals(item0.getDescription(), retrievedItem.getDescription());
    }

    @Test
    public void testGetItemsByName() {
        Item item0 = createFirstItem();
        List<Item> items = new ArrayList<>(2);
        items.add(item0);
        when(itemRepository.findByName(ITEM_TO_ADD)).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(ITEM_TO_ADD);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> retrievedItems = response.getBody();
        assertNotNull(retrievedItems);
        assertEquals(1, retrievedItems.size());
        assertEquals(item0, retrievedItems.get(0));
    }

    @Test
    public void testSubmit() {
        Result result = getResult();
        Item item = createFirstItem();
        Cart cart = getCart(item, result);
        result.user.setCart(cart);
        when(userRepo.findByUsername(result.username)).thenReturn(result.user);
        ResponseEntity<UserOrder> response = orderController.submit(result.username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder retrievedUserOrder = response.getBody();
        assertNotNull(retrievedUserOrder);
        assertNotNull(retrievedUserOrder.getItems());
        assertNotNull(retrievedUserOrder.getTotal());
        assertNotNull(retrievedUserOrder.getUser());
    }

    @Test
    public void testSubmitNullUser() {
        Result result = getResult();
        Item item = createFirstItem();
        Cart cart = getCart(item, result);
        result.user.setCart(cart);
        when(userRepo.findByUsername(result.username)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(result.username);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUser() {
        Result result = getResult();
        Item item = createFirstItem();
        Cart cart = getCart(item, result);
        result.user.setCart(cart);
        when(userRepo.findByUsername(result.username)).thenReturn(result.user);
        orderController.submit(result.username);
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(result.username);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        List<UserOrder> userOrders = responseEntity.getBody();
        assertNotNull(userOrders);
        assertEquals(0, userOrders.size());
    }

    @Test
    public void testGetOrdersForUserNullUser() {
        Result result = getResult();
        Item item = createFirstItem();
        Cart cart = getCart(item, result);
        result.user.setCart(cart);
        when(userRepo.findByUsername(result.username)).thenReturn(null);
        orderController.submit(result.username);
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(result.username);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

}

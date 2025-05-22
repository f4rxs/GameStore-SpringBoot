package com.example.gamestore.services;

import com.example.gamestore.models.Cart;
import com.example.gamestore.models.Game;
import com.example.gamestore.models.Order;
import com.example.gamestore.models.OrderItem;
import com.example.gamestore.repositories.OrderItemRepository;
import com.example.gamestore.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final GameService gameService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartService cartService,
                        GameService gameService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.gameService = gameService;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderById(int id) {
        return orderRepository.findById(id);
    }

    public List<Order> findOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Order saveOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        }
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromCart(int userId) {
        List<Cart> cartItems = cartService.findCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Calculate total
        double total = 0;
        for (Cart item : cartItems) {
            Optional<Game> gameOpt = gameService.findGameById(item.getGameId());
            if (gameOpt.isPresent()) {
                Game game = gameOpt.get();
                total += game.getPrice() * item.getQuantity();

                // Check stock
                if (game.getStock() < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for game: " + game.getTitle());
                }
            } else {
                throw new IllegalStateException("Game not found with ID: " + item.getGameId());
            }
        }

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setTotal(total);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        // Create order items and update stock
        for (Cart item : cartItems) {
            Optional<Game> gameOpt = gameService.findGameById(item.getGameId());
            if (gameOpt.isPresent()) {
                Game game = gameOpt.get();

                // Create order item
                OrderItem orderItem = new OrderItem(
                        savedOrder.getId(),
                        game.getId(),
                        item.getQuantity(),
                        game.getPrice()
                );
                orderItemRepository.save(orderItem);

                // Update stock
                gameService.updateGameStock(game.getId(), item.getQuantity());
            }
        }

        // Clear cart
        cartService.clearCart(userId);

        return savedOrder;
    }

    public Order updateOrderStatus(int orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    public List<OrderItem> findOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findByIdOrderId(orderId);
    }
}
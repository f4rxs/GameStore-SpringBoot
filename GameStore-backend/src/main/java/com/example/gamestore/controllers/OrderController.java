package com.example.gamestore.controllers;

import com.example.gamestore.models.Order;
import com.example.gamestore.models.OrderItem;
import com.example.gamestore.services.OrderItemService;
import com.example.gamestore.services.OrderService;
import com.example.gamestore.utils.OrderWithItemsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        logger.info("OrderController initialized");
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderService.findAllOrders();
        logger.info("Found {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        logger.info("Fetching order with id: {}", id);
        Optional<Order> order = orderService.findOrderById(id);
        if (order.isPresent()) {
            logger.info("Found order with id: {}", id);
            return ResponseEntity.ok(order.get());
        } else {
            logger.warn("Order not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable int userId) {
        logger.info("Fetching orders for user id: {}", userId);
        List<Order> orders = orderService.findOrdersByUserId(userId);
        logger.info("Found {} orders for user id: {}", orders.size(), userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        logger.info("Fetching orders with status: {}", status);
        List<Order> orders = orderService.findOrdersByStatus(status);
        logger.info("Found {} orders with status: {}", orders.size(), status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable int id) {
        logger.info("Fetching items for order id: {}", id);
        List<OrderItem> orderItems = orderService.findOrderItemsByOrderId(id);
        logger.info("Found {} items for order id: {}", orderItems.size(), id);
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Order> createOrderFromCart(@PathVariable int userId) {
        logger.info("Creating order from cart for user id: {}", userId);
        try {
            Order order = orderService.createOrderFromCart(userId);
            logger.info("Successfully created order {} for user {}", order.getId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalStateException e) {
            logger.error("Failed to create order from cart for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/with-items") // NEEDS TESTING
    public ResponseEntity<Order> createOrderWithItems(@RequestBody OrderWithItemsRequest request) {
        logger.info("Creating new order with {} items", request.getItems().size());
        try {
            Order savedOrder = orderService.saveOrder(request.getOrder());
            for (OrderItem item : request.getItems()) {
                item.setOrderId(savedOrder.getId());
                orderItemService.saveOrderItem(item);
            }
            logger.info("Successfully created order {} with {} items", savedOrder.getId(), request.getItems().size());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            logger.error("Failed to create order with items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable int id, @RequestParam String status) {
        logger.info("Updating status to {} for order id: {}", status, id);
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            logger.info("Successfully updated status for order {}", id);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update status for order {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable int id, @RequestBody Order order) {
        logger.info("Updating order id: {}", id);
        order.setId(id);
        Order updatedOrder = orderService.saveOrder(order);
        logger.info("Successfully updated order {}", id);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        logger.info("Deleting order id: {}", id);
        orderService.deleteOrder(id);
        logger.info("Successfully deleted order {}", id);
        return ResponseEntity.noContent().build();
    }
}
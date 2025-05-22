package com.example.gamestore.controllers;



import com.example.gamestore.models.OrderItem;
import com.example.gamestore.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orderitems")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.findAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable int orderId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByGameId(@PathVariable int gameId) {
        List<OrderItem> orderItems = orderItemService.findOrderItemsByGameId(gameId);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/order/{orderId}/game/{gameId}")
    public ResponseEntity<OrderItem> getOrderItem(@PathVariable int orderId, @PathVariable int gameId) {
        Optional<OrderItem> orderItem = orderItemService.findOrderItem(orderId, gameId);
        return orderItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderItem);
    }

    @PutMapping
    public ResponseEntity<OrderItem> updateOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem updatedOrderItem = orderItemService.saveOrderItem(orderItem);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/order/{orderId}/game/{gameId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable int orderId, @PathVariable int gameId) {
        orderItemService.deleteOrderItem(orderId, gameId);
        return ResponseEntity.noContent().build();
    }
}
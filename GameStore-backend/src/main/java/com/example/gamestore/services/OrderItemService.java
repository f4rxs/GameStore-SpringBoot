package com.example.gamestore.services;



import com.example.gamestore.models.OrderItem;
import com.example.gamestore.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> findAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> findOrderItem(int orderId, int gameId) {
        OrderItem.OrderItemId id = new OrderItem.OrderItemId(orderId, gameId);
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findByIdOrderId(orderId);
    }

    public List<OrderItem> findOrderItemsByGameId(int gameId) {
        return orderItemRepository.findByIdGameId(gameId);
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(int orderId, int gameId) {
        OrderItem.OrderItemId id = new OrderItem.OrderItemId(orderId, gameId);
        orderItemRepository.deleteById(id);
    }
}
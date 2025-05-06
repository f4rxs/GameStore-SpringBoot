package com.repositories;

import com.models.OrderItem;
import com.models.OrderItem.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByIdOrderId(int orderId);
    List<OrderItem> findByIdGameId(int gameId);
}
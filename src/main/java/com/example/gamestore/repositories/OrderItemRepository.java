package com.example.gamestore.repositories;

import com.example.gamestore.models.OrderItem;
import com.example.gamestore.models.OrderItem.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByIdOrderId(int orderId);
    List<OrderItem> findByIdGameId(int gameId);
    @Query("SELECT oi FROM OrderItem oi JOIN Order o ON oi.id.orderId = o.id WHERE o.userId = :userId")
    List<OrderItem> findByUserOrders(@Param("userId") int userId);
}
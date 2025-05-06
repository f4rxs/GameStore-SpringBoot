package com.example.gamestore.models;
import jakarta.persistence.*;

@Entity
@Table(name = "orderitems")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    private int quantity;

    @Column(name = "unit_price")
    private double unitPrice;

    public OrderItem() {}

    public OrderItem(int orderId, int gameId, int quantity, double unitPrice) {
        this.id = new OrderItemId(orderId, gameId);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Composite key class
    @Embeddable
    public static class OrderItemId implements java.io.Serializable {
        @Column(name = "order_id")
        private int orderId;

        @Column(name = "game_id")
        private int gameId;

        public OrderItemId() {}

        public OrderItemId(int orderId, int gameId) {
            this.orderId = orderId;
            this.gameId = gameId;
        }

        // equals and hashCode methods
    }

    // Getters and setters
    public int getOrderId() {
        return id.orderId;
    }

    public void setOrderId(int orderId) {
        if (this.id == null) {
            this.id = new OrderItemId();
        }
        this.id.orderId = orderId;
    }

    public int getGameId() {
        return id.gameId;
    }

    public void setGameId(int gameId) {
        if (this.id == null) {
            this.id = new OrderItemId();
        }
        this.id.gameId = gameId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "OrderItem{orderId=" + id.orderId + ", gameId=" + id.gameId + ", quantity=" + quantity + "}";
    }
}

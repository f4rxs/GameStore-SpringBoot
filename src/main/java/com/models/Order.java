package com.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    private double total;

    @Column(name = "order_date")
    private Timestamp orderDate;

    private String status;

    public Order() {}

    public Order(int id, int userId, double total, Timestamp orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and setters remain the same
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", total=" + total + ", status='" + status + "'}";
    }

}

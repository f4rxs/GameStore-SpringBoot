package com.example.gamestore.utils;

import com.example.gamestore.models.Order;
import com.example.gamestore.models.OrderItem;

import java.util.List;

public  class OrderWithItemsRequest {
    private Order order;
    private List<OrderItem> items;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
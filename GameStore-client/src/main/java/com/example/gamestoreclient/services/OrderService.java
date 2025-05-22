package com.example.gamestoreclient.services;

import com.example.gamestoreclient.config.ApiConfig;
import com.example.gamestoreclient.models.Order;
import com.example.gamestoreclient.models.OrderItem;
import com.example.gamestoreclient.utils.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class OrderService {
    public List<Order> getAllOrders() throws IOException {
        Type orderListType = new TypeToken<List<Order>>(){}.getType();
        return HttpUtils.getList(ApiConfig.ORDERS_URL, orderListType);
    }

    public Order getOrderById(int id) throws IOException {
        return HttpUtils.get(ApiConfig.ORDERS_URL + "/" + id, Order.class);
    }

    public List<Order> getOrdersByUserId(int userId) throws IOException {
        Type orderListType = new TypeToken<List<Order>>(){}.getType();
        return HttpUtils.getList(ApiConfig.ORDERS_URL + "/user/" + userId, orderListType);
    }

    public List<Order> getOrdersByStatus(String status) throws IOException {
        Type orderListType = new TypeToken<List<Order>>(){}.getType();
        return HttpUtils.getList(ApiConfig.ORDERS_URL + "/status/" + status, orderListType);
    }

    public List<OrderItem> getOrderItems(int orderId) throws IOException {
        Type orderItemListType = new TypeToken<List<OrderItem>>(){}.getType();
        return HttpUtils.getList(ApiConfig.ORDERS_URL + "/" + orderId + "/items", orderItemListType);
    }

    public Order createOrderFromCart(int userId) throws IOException {
        return HttpUtils.post(ApiConfig.ORDERS_URL + "/user/" + userId, null, Order.class);
    }

    public Order updateOrderStatus(int id, String status) throws IOException {
        return HttpUtils.put(ApiConfig.ORDERS_URL + "/" + id + "/status?status=" + status, null, Order.class);
    }

    public Order updateOrder(Order order) throws IOException {
        return HttpUtils.put(ApiConfig.ORDERS_URL + "/" + order.getId(), order, Order.class);
    }

    public void deleteOrder(int id) throws IOException {
        HttpUtils.delete(ApiConfig.ORDERS_URL + "/" + id);
    }
}

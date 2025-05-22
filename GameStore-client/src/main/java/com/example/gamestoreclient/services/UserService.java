package com.example.gamestoreclient.services;



import com.example.gamestoreclient.config.ApiConfig;
import com.example.gamestoreclient.models.LoginRequest;
import com.example.gamestoreclient.models.User;
import com.example.gamestoreclient.utils.HttpUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;




public class UserService {

    public List<User> getAllUsers() throws IOException {
        Type userListType = new TypeToken<List<User>>(){}.getType();
        return HttpUtils.getList(ApiConfig.USERS_URL, userListType);
    }

    public User getUserById(int id) throws IOException {
        return HttpUtils.get(ApiConfig.USERS_URL + "/" + id, User.class);
    }

    public User getUserByEmail(String email) throws IOException {
        return HttpUtils.get(ApiConfig.USERS_URL + "/email/" + email, User.class);
    }

    public User registerUser(User user) throws IOException {
        return HttpUtils.post(ApiConfig.USERS_URL + "/register", user, User.class);
    }

    public User loginUser(String email, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(email, password);
        return HttpUtils.post(ApiConfig.USERS_URL + "/login", loginRequest, User.class);
    }

    public User updateUser(User user) throws IOException {
        return HttpUtils.put(ApiConfig.USERS_URL + "/" + user.getId(), user, User.class);
    }

    public void deleteUser(int id) throws IOException {
        HttpUtils.delete(ApiConfig.USERS_URL + "/" + id);
    }
}

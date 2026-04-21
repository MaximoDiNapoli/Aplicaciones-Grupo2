package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;

public interface UserService {

    public List<UserResponse> getUsers(String rol, String ciudad, String codigopostal);

    public UserResponse getUserById(Integer id);

    public UserResponse updateUser(Integer id, UserRequest userDetails);

    public void deleteUser(Integer id);

    public UserResponse getCurrentUser();

    public UserResponse updateCurrentUser(UserRequest userDetails);
}

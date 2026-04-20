package com.ecomerce.src.service;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;

import java.util.List;

public interface UserService {

    public List<UserResponse> getUsers(String rol, String ciudad, String codigopostal);
    public UserResponse getUserById(Long id);

    public UserResponse updateUser(Long id, UserRequest userDetails);

    public void deleteUser(Long id);
}

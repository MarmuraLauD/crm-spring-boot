package com.gym.crmspringboot.service;

public interface UserService {

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, Boolean status);

}

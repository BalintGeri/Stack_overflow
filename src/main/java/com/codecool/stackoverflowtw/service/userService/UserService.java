package com.codecool.stackoverflowtw.service.userService;

import com.codecool.stackoverflowtw.controller.dto.NewUserDTO;
import com.codecool.stackoverflowtw.controller.dto.UserDTO;
import com.codecool.stackoverflowtw.dao.UserDao;
import com.codecool.stackoverflowtw.dao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    UserDao userDao;
    UserConverter userConverter;

    @Autowired
    public UserService(UserDao userDao, UserConverter userConverter) {
        this.userDao = userDao;
        this.userConverter = userConverter;
    }

    public List<UserDTO> getAllUsers() {
        List<User> allUsers = userDao.getAllUsers();
        return userConverter.convert(allUsers);
    }

    public void createNewUser(NewUserDTO newUserDTO) throws UserAlreadyExistAuthenticationException {
        User newUser = userConverter.convertNewUserDtoToUser(newUserDTO);
        boolean isUserCreated = userDao.createUser(newUser);

        if (!isUserCreated) {
            throw new UserAlreadyExistAuthenticationException("Username already taken");
        }
    }
}

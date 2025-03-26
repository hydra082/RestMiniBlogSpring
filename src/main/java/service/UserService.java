package service;

import dao.UserDAO;
import dto.UserDTO;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDAO userDAO);
    UserDTO updateUser(UserDAO userDAO);
    void deleteUser(Long id);
}

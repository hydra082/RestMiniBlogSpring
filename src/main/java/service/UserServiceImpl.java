package service;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import mapper.UserMapper;
import repository.UserRepository;

import static util.Utils.NOT_FOUND;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + id + NOT_FOUND);
        }
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO createUser(UserDAO userDAO) {
        User user = userMapper.toEntity(userDAO);
        User savedUser = userRepository.save(user);
        if (savedUser == null) {
            throw new IllegalStateException("Failed to save user");
        }
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDAO userDAO) {
        User user = userMapper.toEntity(userDAO);
        if (user.getId() == null) {
            throw new IllegalArgumentException("User id must not be null for update");
        }
        userRepository.update(user);
        return userMapper.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}

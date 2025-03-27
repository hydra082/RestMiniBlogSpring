package service;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("Test");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test");

        when(userRepository.findById(1L)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("Test"));
        verify(userRepository).findById(1L);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldCreateUser() {
        UserDAO userDAO = new UserDAO();
        userDAO.setName("Test");

        User user = new User();
        user.setName("Test");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test");

        when(userMapper.toEntity(userDAO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDAO);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(1L));
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void shouldUpdateUser() {
        UserDAO userDAO = new UserDAO();
        userDAO.setId(1L);
        userDAO.setName("Updated");

        User user = new User();
        user.setId(1L);
        user.setName("Updated");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Updated");

        when(userMapper.toEntity(userDAO)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        doNothing().when(userRepository).update(any(User.class));

        UserDTO result = userService.updateUser(userDAO);

        assertThat(result.getName(), is("Updated"));
        verify(userRepository).update(any(User.class));
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).delete(1L);
        userService.deleteUser(1L);
        verify(userRepository).delete(1L);
    }
}

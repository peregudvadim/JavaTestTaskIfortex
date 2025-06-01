package com.example.java_ifortex_test_task.service;

import com.example.java_ifortex_test_task.dto.UserResponseDTO;
import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.Session;
import com.example.java_ifortex_test_task.entity.User;
import com.example.java_ifortex_test_task.mapper.UserMapper;
import com.example.java_ifortex_test_task.repository.SessionRepository;
import com.example.java_ifortex_test_task.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Returns a User with the biggest amount of sessions
    public UserResponseDTO getUserWithMostSessions() {
        List<Object[]> results = userRepository.getUserWithMostSessions();
        if (results == null || results.isEmpty() || results.get(0) == null) {
            throw new EntityNotFoundException("No user found");
        }
        Object[]result = results.get(0);

        User user = mapToUser(result);
        return userMapper.toDto(user);
    }

    // Returns Users that have at least 1 Mobile session
    public List<UserResponseDTO> getUsersWithAtLeastOneMobileSession() {

        List<Object[]> results = userRepository.getUsersWithAtLeastOneMobileSession(DeviceType.MOBILE.getCode());
        if (results == null || results.isEmpty() || results.get(0) == null) {
            throw new EntityNotFoundException("No user found");
        }

        List<User> users = new ArrayList<>();
        for (Object[] obj : results) {

            users.add(mapToUser(obj));
        }

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private User mapToUser(Object[] obj) {
        if (obj == null || obj.length < 6) {
            throw new IllegalArgumentException("Invalid result array for mapping User");
        }
        User user = new User();
        user.setId(((Number) obj[0]).longValue());       // user_id
        user.setFirstName((String) obj[1]);              // user_first_name
        user.setLastName((String) obj[2]);               // user_last_name
        user.setMiddleName((String) obj[3]);             // user_middle_name
        user.setEmail((String) obj[4]);                  // user_email
        user.setDeleted((Boolean) obj[5]);               // user_deleted
        return user;
    }
}




package com.onion.backend.service;

import com.onion.backend.dto.SignUpUser;
import com.onion.backend.entity.User;
import com.onion.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 클라이언트한테 받아온데이터를 DB에 저장 하기 위해 데이터 변환
    public User createUser(SignUpUser signUpUser) {
        User user = new User();
        user.setUsername(signUpUser.getUsername());
        user.setPassword(passwordEncoder.encode(signUpUser.getPassword()));
        user.setEmail(signUpUser.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
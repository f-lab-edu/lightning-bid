package com.lightningbid.user.service;

import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow();
    }
}

package com.lightningbid.auth.service;

import com.lightningbid.auth.config.JwtUtil;
import com.lightningbid.auth.dto.TokensDto;
import com.lightningbid.auth.enums.Role;
import com.lightningbid.auth.exception.*;
import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.domain.repository.UserRepository;
import com.lightningbid.user.web.dto.request.SignupRequestDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public TokensDto completeSocialSignup(String authorizationHeader, SignupRequestDto requestDto) {

        String signupToken = authorizationHeader.replace("Bearer ", "");

        String username = jwtUtil.getUsername(signupToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (Role.ROLE_GUEST != user.getRole())
            throw new UserAlreadyRegisteredException();

        if (userRepository.existsByNickname(requestDto.getNickname()))
            throw new NicknameAlreadyInUseException("이미 사용 중인 닉네임입니다. 입력: " + requestDto.getNickname());

        user.completeSignup(requestDto.getNickname(), "");

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getRole().name(), user.getNickname(), user.getProfileUrl());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername(), user.getRole().name());

        return TokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokensDto refreshAccessToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank())
            throw new RefreshTokenMissingException();

        try {
            jwtUtil.verifyToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        } catch (JwtException e) {
            throw new RefreshTokenException();
        }

        String username = jwtUtil.getUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getRole().name(), user.getNickname(), user.getProfileUrl());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getUsername(), user.getRole().name());

        return TokensDto.builder()
                .refreshToken(newRefreshToken)
                .accessToken(accessToken)
                .build();
    }
}

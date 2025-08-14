package com.lightningbid.auth.service;

import com.lightningbid.auth.config.JwtUtil;
import com.lightningbid.auth.dto.TokensDto;
import com.lightningbid.auth.enums.Role;
import com.lightningbid.auth.exception.*;
import com.lightningbid.common.exception.BaseException;
import com.lightningbid.file.domain.model.File;
import com.lightningbid.file.service.FileService;
import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.domain.repository.UserRepository;
import com.lightningbid.user.web.dto.request.SignupRequestDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final FileService fileService;

    private final JwtUtil jwtUtil;


    @Transactional
    public TokensDto completeSocialSignUp(String signUpToken, SignupRequestDto requestDto) {

        validateToken(signUpToken,
                SignUpTokenMissingException::new,
                SignUpTokenExpiredException::new,
                SignUpTokenException::new
        );

        User user = getUser(signUpToken);

        if (Role.ROLE_GUEST != user.getRole())
            throw new UserAlreadyRegisteredException();

        if (userRepository.existsByNickname(requestDto.getNickname()))
            throw new NicknameAlreadyInUseException("이미 사용 중인 닉네임입니다. 입력: " + requestDto.getNickname());

        File file = fileService.findFileByUuid(requestDto.getImageUuid());
        file.updateUser(user);

        user.completeSignup(requestDto.getNickname());

        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public TokensDto completeSocialSignIn(String signInToken) {

        validateToken(signInToken,
                SignInTokenMissingException::new,
                SignInTokenExpiredException::new,
                SignInTokenException::new
        );
        return issueTokens(getUser(signInToken));
    }

    @Transactional(readOnly = true)
    public TokensDto refreshAccessToken(String refreshToken) {

        validateToken(refreshToken,
                RefreshTokenMissingException::new,
                RefreshTokenExpiredException::new,
                RefreshTokenException::new
        );
        return issueTokens(getUser(refreshToken));
    }

    private void validateToken(String token,
                               Supplier<? extends BaseException> missingExceptionSupplier,
                               Supplier<? extends BaseException> expiredExceptionSupplier,
                               Supplier<? extends BaseException> generalExceptionSupplier) {

        if (token == null || token.isBlank())
            throw missingExceptionSupplier.get();

        try {
            jwtUtil.verifyToken(token);
        } catch (ExpiredJwtException e) {
            throw expiredExceptionSupplier.get();
        } catch (JwtException e) {
            throw generalExceptionSupplier.get();
        }
    }

    private User getUser(String token) {
        String username = jwtUtil.getUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private TokensDto issueTokens(User user) {
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getRole().name(), user.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername(), user.getRole().name());

        return TokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

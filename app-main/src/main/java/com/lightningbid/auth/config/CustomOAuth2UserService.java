package com.lightningbid.auth.config;

import com.lightningbid.auth.enums.Role;
import com.lightningbid.user.domain.model.User;
import com.lightningbid.user.domain.repository.UserRepository;
import com.lightningbid.auth.dto.UserDto;
import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.auth.dto.NaverResponse;
import com.lightningbid.auth.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User attributes: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } /* else if (registrationId.equals("google")) {

        } */
        else {
            log.warn("지원하지 않는 소셜 로그인입니다: {}", registrationId);
            // TODO 예외처리 추가
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        
        Optional<User> findUserOptional = userRepository.findByUsername(username);
        User user;

        if (findUserOptional.isEmpty()) {
            // 사용자가 없는 경우 -> 새로 생성 (회원가입)
            user = userRepository.save(User.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .phone(oAuth2Response.getMobile())
                    .provider(oAuth2Response.getProvider())
                    .providerId(oAuth2Response.getProviderId())
                    .role(Role.ROLE_GUEST)
                    .build());

        } else {
            // 사용자가 있는 경우 -> 정보 업데이트
            user = findUserOptional.get();
            user.updateOAuthInfo(
                    oAuth2Response.getName(),
                    oAuth2Response.getMobile(),
                    oAuth2Response.getEmail()
            );
        }

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .name(user.getName())
                .role(user.getRole())
                .build();

        return CustomOAuth2User.builder()
                .userDto(userDto)
                .attributes(oAuth2User.getAttributes())
                .build();
    }
}

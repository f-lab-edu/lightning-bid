package com.lightningbid.auth.dto;

public interface OAuth2Response {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    String getMobile();
    String getProfileImage();
    String getNickname();
}

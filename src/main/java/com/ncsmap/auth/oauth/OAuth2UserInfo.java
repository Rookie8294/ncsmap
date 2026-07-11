package com.ncsmap.auth.oauth;

public interface OAuth2UserInfo {

    String getProviderId();

    String getEmail();

    String getName();

    String getNickname();

    String getProfileImage();
}

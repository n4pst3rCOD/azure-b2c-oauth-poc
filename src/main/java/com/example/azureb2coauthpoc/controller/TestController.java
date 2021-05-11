package com.example.azureb2coauthpoc.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testLogin(OAuth2AuthenticationToken token){
        if (token != null) {
            final OAuth2User user = token.getPrincipal();
            int a = 10;
        }
        return "hello";
    }
}

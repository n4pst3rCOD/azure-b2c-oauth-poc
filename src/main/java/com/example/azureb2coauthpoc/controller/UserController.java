package com.example.azureb2coauthpoc.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@RestController
public class UserController {

    private static BiFunction<Map, String, Object> GET_ATTRIBUTE_VALUE =
            (m, k) -> m.getOrDefault(k, null);

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticateAndReturnJWT(OAuth2AuthenticationToken token) throws UnsupportedEncodingException {
        Optional<OAuth2User> userOptional = Optional.ofNullable(token.getPrincipal());

        String userName = userOptional.map(OAuth2User::getAttributes)
                .map(attributes -> (String) GET_ATTRIBUTE_VALUE.apply(attributes, "name"))
                .orElse(null);

        String email = (String) userOptional.map(OAuth2User::getAttributes)
                .map(attributes -> (List) GET_ATTRIBUTE_VALUE.apply(attributes, "emails"))
                .map(List::stream)
                .map(Stream::findFirst)
                .map(val -> val.orElse(null))
                .orElse(null);


        String jwtToken = Jwts.builder()
                .setIssuer("b2c-demo-service")
                .setSubject(userName)
                .claim("role", "admin")
                .claim("email", email)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(System.currentTimeMillis())))
                .setExpiration(Date.from(Instant.ofEpochSecond(System.currentTimeMillis() + 100000000L)))
                .signWith(
                        SignatureAlgorithm.HS256,
                        jwtSecretKey.getBytes("UTF-8")
                ).compact();

        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("jwt-token", jwtToken);
        }});
    }
}

package com.thinktag.jwt.controller;

import com.thinktag.jwt.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JwtController {

    @NonNull
    @Autowired
    JwtTokenService tokenService;

    @PostMapping("api/token/create")
    @ResponseBody
    public String createToken(
            @NotNull @RequestParam("username") final String username) {
        final Map<String, String> map = new HashMap<>();
        map.put("username", username);
        return tokenService.expiring(map);
    }

    @PostMapping("api/token/validate")
    @ResponseBody
    public Map<String, String> validateToken(
            @NotNull @RequestParam("token") final String token) {
        return tokenService.trusted(token);
    }
}

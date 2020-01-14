package com.thinktag.jwt.service;

import java.util.Map;

public interface JwtTokenService {

    String permanent(Map<String, String> attributes);

    String expiring(Map<String, String> attributes);

    /**
     * Checks the validity of the given credentials.
     *
     * @param token
     * @return attributes if verified
     */
    Map<String, String> untrusted(String token);

    /**
     * Checks the validity of the given credentials.
     *
     * @param token
     * @return attributes if verified
     */
    Map<String, String> trusted(String token);
}

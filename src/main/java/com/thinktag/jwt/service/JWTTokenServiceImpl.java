package com.thinktag.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Service
public class JWTTokenServiceImpl implements Clock, JwtTokenService {

    private static final String DOT = ".";

    private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

    String issuer;
    int expirationSec;
    int clockSkewSec;
    String secretKey;

    JWTTokenServiceImpl(@Value("${jwt.issuer:thinktag}") final String issuer,
                    @Value("${jwt.expiration-sec:1800}") final int expirationSec,
                    @Value("${jwt.clock-skew-sec:300}") final int clockSkewSec,
                    @Value("${jwt.secret:jwt-secret}") final String secret) {
        super();
        this.issuer = requireNonNull(issuer);
        this.expirationSec = requireNonNull(expirationSec);
        this.clockSkewSec = requireNonNull(clockSkewSec);
        this.secretKey = BASE64.encode(requireNonNull(secret));
    }

    @Override
    public String permanent(final Map<String, String> attributes) {
        return newToken(attributes, 0);
    }

    @Override
    public String expiring(final Map<String, String> attributes) {
        return newToken(attributes, expirationSec);
    }

    private String newToken(final Map<String, String> attributes, final int expiresInSec) {
        final DateTime now = Instant.now().toDateTime();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(now.toDate());

        if (expiresInSec > 0) {
            final DateTime expiresAt = now.plusSeconds(expiresInSec);
            claims.setExpiration(expiresAt.toDate());
        }
        claims.putAll(attributes);

        return Jwts
                .builder()
                .setClaims(claims)
                //HMAC using SHA-256
                .signWith(HS256, secretKey)
                .compressWith(COMPRESSION_CODEC)
                .compact();
    }

    @Override
    public Map<String, String> trusted(final String token) {
        final JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey);
        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    @Override
    public Map<String, String> untrusted(final String token) {
        final JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec);

        // See: https://github.com/jwtk/jjwt/issues/135
        final String withoutSignature = substringBeforeLast(token, DOT) + DOT;
        return parseClaims(() -> parser.parseClaimsJwt(withoutSignature).getBody());
    }

    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final Map builder = new HashMap();
            for (final Map.Entry<String, Object> e: claims.entrySet()) {
                builder.put(e.getKey(), String.valueOf(e.getValue()));
            }
            return Collections.unmodifiableMap(builder);
        } catch (final IllegalArgumentException | JwtException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public Date now() {
        final DateTime now = Instant.now().toDateTime();
        return now.toDate();
    }
}

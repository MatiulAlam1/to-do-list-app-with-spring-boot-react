package com.in28minutes.rest.webservices.restfulwebservices.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.utils.AttributeMap.Key;

@Service
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;
	private JwtDecoder jwtDecoder;

	public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;

	}

	public String generateToken(Authentication authentication, int duration, String tokenType) {

		String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(Instant.now())
				.expiresAt(Instant.now().plus(duration, ChronoUnit.MINUTES)).subject(authentication.getName())
				.claim("scope", scope).claim("type",tokenType).build();

		return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public Authentication validateRefreshToken(String refreshToken) {
	    if (!isRefreshToken(refreshToken)) {
	        throw new IllegalArgumentException("Invalid token type");
	    }
		try {
			
			org.springframework.security.oauth2.jwt.Jwt jwt = jwtDecoder.decode(refreshToken);
			return new JwtAuthenticationToken(jwt);
		} catch (JwtException e) {
			throw new RuntimeException("Invalid refresh token", e);
		}
	}
	
//	public boolean isAccessToken(String token) {
//	    Map<String, Object> claims = jwtDecoder.decode(token).getClaims();
//	    String type = (String) claims.get("type");
//	    return "access".equals(type);
//	}

	public boolean isRefreshToken(String token) {
	    Map<String, Object> claims = jwtDecoder.decode(token).getClaims();
	    String type = (String) claims.get("type");
	    return "refresh".equals(type);
	}
}

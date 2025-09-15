package com.in28minutes.rest.webservices.restfulwebservices.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.http.HttpStatusCode;

@RestController
@RequestMapping("/api")
public class JwtAuthenticationController {

	private final JwtTokenService tokenService;

	private Logger l = LoggerFactory.getLogger(getClass());

	private final AuthenticationManager authenticationManager;

	public JwtAuthenticationController(JwtTokenService tokenService, AuthenticationManager authenticationManager) {
		this.tokenService = tokenService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> generateToken(@RequestBody JwtTokenRequest jwtTokenRequest) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(jwtTokenRequest.getUsername(),
				jwtTokenRequest.getPassword());

		var authentication = authenticationManager.authenticate(authenticationToken);

		var token = tokenService.generateToken(authentication, 90, "access");
		final String refreshToken = tokenService.generateToken(authentication, 540, "refresh");

		return ResponseEntity.ok(new JwtTokenResponse(token, refreshToken));

	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		String refreshToken = refreshTokenRequest.getRefreshToken();
		// Validate refresh token and extract authentication details
		var authentication = tokenService.validateRefreshToken(refreshToken);

		// Generate a new access token
		String newAccessToken = tokenService.generateToken(authentication, 90, "access"); // 90 minutes expiration or desired
																				// duration

		return ResponseEntity.ok(new JwtTokenResponse(newAccessToken, refreshToken)); // Return the same or new
																						// refresh token as needed

	}
}

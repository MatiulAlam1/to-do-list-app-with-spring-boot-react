package com.in28minutes.rest.webservices.restfulwebservices.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public CustomAuthenticationEntryPoint() {
        System.out.println("CustomAuthenticationEntryPoint initialized");
    }

	@Override
	public void commence(HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		// TODO Auto-generated method stub]
        System.out.println("CustomAuthenticationEntryPoint triggered");

        System.out.println("authException.getMessage()"+authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = String.format("{\"error\": \"Authentication failed: %s\"}", authException.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
	}
}
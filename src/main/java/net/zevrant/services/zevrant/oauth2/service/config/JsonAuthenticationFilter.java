package net.zevrant.services.zevrant.oauth2.service.config;

import org.springframework.security.authentication.AuthenticationManager;
import net.zevrant.services.zevrant.oauth2.service.rest.request.LoginRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;


public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JsonAuthenticationFilter.class);

    private ObjectMapper objectMapper;

    public JsonAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.objectMapper = new ObjectMapper();
        this.setAuthenticationManager(authenticationManager);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("attemptAuthentication");
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest = this.getUserNamePasswordAuthenticationToken(request);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getUserNamePasswordAuthenticationToken(HttpServletRequest request) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
            LoginRequest loginRequest = objectMapper.readValue(inputStream, LoginRequest.class);
            return new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        } catch (IOException e) {
            throw new AuthenticationServiceException("failed to parse json body");
        }
    }

}
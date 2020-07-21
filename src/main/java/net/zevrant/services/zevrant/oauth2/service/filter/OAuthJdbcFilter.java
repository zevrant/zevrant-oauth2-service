package net.zevrant.services.zevrant.oauth2.service.filter;

import net.zevrant.services.zevrant.oauth2.service.config.AuthenticationManager;
import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class OAuthJdbcFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(OAuthJdbcFilter.class);

    private final UserProvider userProvider;
    private final AuthenticationManager authenticationManager;

    public OAuthJdbcFilter(UserProvider userProvider, AuthenticationManager authenticationManager,
                           ClientDetailsService clientDetailsService) {
        this.userProvider = userProvider;
        this.authenticationManager = authenticationManager;
        authenticationManager.setClientDetailsService(clientDetailsService);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authorization = ((HttpServletRequest) request).getHeader("Authorization").split(" ")[1];
    ClientDetails details;
    try {
        details = userProvider.loadClientByToken(authorization);
    } catch(ClientRegistrationException ex) {
      logger.warn("failed to find authorization for user token");
      chain.doFilter(request, response);
      return;
    }
    if(details != null) {
      OAuth2Request oauthRequest = new OAuth2Request(details.getClientId());
      OAuth2Authentication newAuthentication = new OAuth2Authentication(oauthRequest, new net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails(details.getClientId(), details.getClientSecret()));
      Authentication authenticated = authenticationManager.authenticate(newAuthentication);
      SecurityContextHolder.getContext().setAuthentication(authenticated);
      String principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }
}

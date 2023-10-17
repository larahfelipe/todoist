package com.larahfelipe.todoist.middleware;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.larahfelipe.todoist.repositories.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ValidateUserMiddleware extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var servletPath = request.getServletPath();

    if (!servletPath.equals("/users/create")) {
      var authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Basic")) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing basic authorization header.");

        return;
      }

      var encodedAuthCredentials = authHeader.substring("Basic".length()).trim();

      if (encodedAuthCredentials == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization credentials.");

        return;
      }

      byte[] decodedAuthCredentials = Base64.getDecoder().decode(encodedAuthCredentials);
      var stringifiedDecodedAuthCredentials = new String(decodedAuthCredentials);

      var authCredentials  = stringifiedDecodedAuthCredentials.split(":");
      var username = authCredentials[0];
      var password = authCredentials[1];

      var userExists = this.userRepository.findByUsername(username);

      if (userExists == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");

        return;
      }

      var isUserPassword = BCrypt.verifyer().verify(password.toCharArray(), userExists.getPassword());

      if (!isUserPassword.verified) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password.");

        return;
      }

      request.setAttribute("userId", userExists.getId());
    }

    filterChain.doFilter(request, response);
  }

}

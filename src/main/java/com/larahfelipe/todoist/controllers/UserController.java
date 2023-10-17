package com.larahfelipe.todoist.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larahfelipe.todoist.models.UserModel;
import com.larahfelipe.todoist.repositories.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  private String messageKey = "message";
  private String userKey = "user";

  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> create(@RequestBody UserModel userData) {
    Map<String, Object> response = new HashMap<>();
    response.put(this.userKey, null);

    var userExists = this.userRepository.findByUsername(userData.getUsername());

    if (userExists != null) {
      response.put(this.messageKey, "User with this username already exists.");

      return ResponseEntity.badRequest().body(response);
    }

    var hashedPassword = BCrypt.withDefaults().hashToString(12, userData.getPassword().toCharArray());
    userData.setPassword(hashedPassword);

    var newUser = this.userRepository.save(userData);
    newUser.setPassword(null);

    response.put(this.userKey, newUser);
    response.put(this.messageKey, "User created successfully.");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}

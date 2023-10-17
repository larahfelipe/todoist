package com.larahfelipe.todoist.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larahfelipe.todoist.models.UserModel;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {

  UserModel findByUsername(String username);

}

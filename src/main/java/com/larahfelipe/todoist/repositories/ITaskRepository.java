package com.larahfelipe.todoist.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larahfelipe.todoist.models.TaskModel;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

  TaskModel findByTitle(String title);
  List<TaskModel> findByUserId(UUID userId);

}

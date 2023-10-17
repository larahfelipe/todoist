package com.larahfelipe.todoist.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larahfelipe.todoist.models.TaskModel;
import com.larahfelipe.todoist.repositories.ITaskRepository;
import com.larahfelipe.todoist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  private String messageKey = "message";
  private String taskKey = "task";
  private String userIdKey = "userId";

  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> create(@RequestBody TaskModel taskData, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(this.taskKey, null);

    var taskExists = this.taskRepository.findByTitle(taskData.getTitle());

    if (taskExists != null) {
      response.put(this.messageKey, "Task with this title already exists.");

      return ResponseEntity.badRequest().body(response);
    }

    var currentDateTime = LocalDateTime.now();

    if (currentDateTime.isAfter(taskData.getStartAt())) {
      response.put(this.messageKey, "Start date must be in the future.");

      return ResponseEntity.badRequest().body(response);
    }

    var startDateBiggerThanEndDate = taskData.getStartAt().isAfter(taskData.getEndAt());

    if (startDateBiggerThanEndDate) {
      response.put(this.messageKey, "Start date must be before end date.");

      return ResponseEntity.badRequest().body(response);
    }

    var userId = request.getAttribute(this.userIdKey);
    taskData.setUserId((UUID) userId);

    var newTask = this.taskRepository.save(taskData);

    response.put(this.taskKey, newTask);
    response.put(this.messageKey, "Task created successfully.");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<Map<String, Object>> update(@RequestBody TaskModel taskData, @PathVariable UUID taskId, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(this.taskKey, null);

    var taskExists = this.taskRepository.findById(taskId).orElse(null);

    if (taskExists == null) {
      response.put(this.messageKey, "Task with this id does not exist.");

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    var userId = request.getAttribute(this.userIdKey);

    if (taskExists.getUserId() != userId) {
      response.put(this.messageKey, "You are not allowed to update this task.");

      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    Utils.copyNonNullObjectKeys(taskData, taskExists);

    var updatedTask = this.taskRepository.save(taskExists);

    response.put(this.taskKey, updatedTask);
    response.put(this.messageKey, "Task updated successfully.");

    return ResponseEntity.ok(response);
  }

  @GetMapping("/all")
  public ResponseEntity<List<TaskModel>> getAll(HttpServletRequest request) {
    var userId = request.getAttribute(this.userIdKey);
    var allTasks = this.taskRepository.findByUserId((UUID) userId);

    return ResponseEntity.ok(allTasks);
  }

}

package com.larahfelipe.todoist.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "tasks")
@Data
public class TaskModel {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  @Column(unique = true, length = 50)
  private String title;

  private String description;
  private String status;

  @Column(length = 10)
  private String priority;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Column(name = "user_id")
  private UUID userId;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public void setTitle(String title) throws IllegalArgumentException {
    if (title.length() > 50)
      throw new IllegalArgumentException("Title must be less than 50 characters.");

    this.title = title;
  }

}

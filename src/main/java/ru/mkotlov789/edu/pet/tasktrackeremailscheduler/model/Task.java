package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    private User user;


}

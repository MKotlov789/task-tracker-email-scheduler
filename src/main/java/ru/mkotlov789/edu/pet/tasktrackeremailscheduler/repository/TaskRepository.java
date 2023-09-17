package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.model.Task;


import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByUserId(Long userId);

}

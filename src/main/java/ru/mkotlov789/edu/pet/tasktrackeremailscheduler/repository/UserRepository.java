package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.model.User;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);

}

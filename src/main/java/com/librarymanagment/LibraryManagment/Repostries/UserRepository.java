package com.librarymanagment.LibraryManagment.Repostries;

import com.librarymanagment.LibraryManagment.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByKeycloakUserId(String keyCloakUserId);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    int deleteUserById(@Param("id") long id);


    boolean existsByKeycloakUserId(String uuid);

}

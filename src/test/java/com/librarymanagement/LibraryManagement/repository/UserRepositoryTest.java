package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.User;
import com.librarymanagement.LibraryManagement.util.AbstractMySqlIntegrationTest;
import com.librarymanagement.LibraryManagement.util.entity.UserTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest extends AbstractMySqlIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Modifying query: delete user by id must delete user and return 1")
    void givenValidUserId_whenDeleteUserById_thenReturn1() {
        User user = entityManager.persistAndFlush(UserTestDataBuilder.getInstance()
                .withoutId()
                .withUsername("ahmedq")
                .withKeycloakUserId("b7f1c2d4-1111")
                .build());

        int rowsAffected = userRepository.deleteUserById(user.getId());
        entityManager.clear();

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("Modifying query: delete user by id must return 0 for invalid id")
    void givenNonValidUserId_whenDeleteUserById_thenReturn0() {
        int rowsAffected = userRepository.deleteUserById(99L);

        assertThat(rowsAffected).isEqualTo(0);
    }
}
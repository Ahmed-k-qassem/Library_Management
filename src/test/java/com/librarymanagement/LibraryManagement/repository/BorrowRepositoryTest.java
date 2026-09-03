package com.librarymanagement.LibraryManagement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
public class BorrowRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BorrowRepository borrowRepository;



}

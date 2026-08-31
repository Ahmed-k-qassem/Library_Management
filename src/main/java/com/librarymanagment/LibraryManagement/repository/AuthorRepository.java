package com.librarymanagment.LibraryManagement.repository;

import com.librarymanagment.LibraryManagement.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAuthorsByNationality(String nationality);

    Optional<Author> findAuthorById(long id);

    @Modifying
    @Query("DELETE FROM Author a WHERE a.id = :id")
    int deleteAuthorById(@Param("id") Long id);

}

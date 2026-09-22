package com.librarymanagement.LibraryManagement.author;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAuthorsByNationality(String nationality);


    @Query(value = """
        SELECT new com.librarymanagement.LibraryManagement.author.AuthorResponseDTO(a.id, a.authorName, a.nationality)
        FROM Author a
        """)
    Page<AuthorResponseDTO> findAllSummaries(Pageable pageable);


    Optional<Author> findAuthorById(long id);

    @Modifying
    @Query("DELETE FROM Author a WHERE a.id = :id")
    int deleteAuthorById(@Param("id") Long id);

}

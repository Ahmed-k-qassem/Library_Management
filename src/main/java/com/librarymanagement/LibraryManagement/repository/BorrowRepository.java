package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO;
import com.librarymanagement.LibraryManagement.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    @Query(value = """
            select new com.librarymanagement.LibraryManagement.dto.Response.BorrowResponseDTO(
                br.id, b.title, c.name, br.borrowDate)
            from Borrow br
            left join br.book b
            left join br.customer c
            """,
            countQuery = "select count(br) from Borrow br")
    Page<BorrowResponseDTO> findAllSummaries(Pageable pageable);
}
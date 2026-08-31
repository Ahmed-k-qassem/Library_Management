package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long>
{
}

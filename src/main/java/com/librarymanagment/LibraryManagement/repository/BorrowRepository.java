package com.librarymanagment.LibraryManagement.repository;

import com.librarymanagment.LibraryManagement.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long>
{
}

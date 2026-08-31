package com.librarymanagment.LibraryManagement.repository;

import com.librarymanagment.LibraryManagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
}

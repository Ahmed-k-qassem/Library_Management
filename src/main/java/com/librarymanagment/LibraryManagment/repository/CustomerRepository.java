package com.librarymanagment.LibraryManagment.repository;

import com.librarymanagment.LibraryManagment.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
}

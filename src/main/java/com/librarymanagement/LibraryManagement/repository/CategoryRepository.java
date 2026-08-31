package com.librarymanagement.LibraryManagement.repository;

import com.librarymanagement.LibraryManagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {



    Category findCategoryByName(String name);


    Optional<Category> getCategoryById(long id);


    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id")
    int deleteCategoryById(@Param("id") long id);
}

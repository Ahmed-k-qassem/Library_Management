package com.librarymanagement.LibraryManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.LibraryManagement.Controller.CategoryController;
import com.librarymanagement.LibraryManagement.security.KeycloakRoleConverter;
import com.librarymanagement.LibraryManagement.service.CategoryService;
import com.librarymanagement.LibraryManagement.service.UserService;
import com.librarymanagement.LibraryManagement.config.SecurityConfig;
import com.librarymanagement.LibraryManagement.dto.Response.CategoryResponseDTO;
import com.librarymanagement.LibraryManagement.exception.GlobalExceptionHandler;
import com.librarymanagement.LibraryManagement.util.dto.request.CategoryRequestDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.dto.response.CategoryResponseDtoTestDataBuilder;
import com.librarymanagement.LibraryManagement.util.entity.PatchBodyBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.admin;
import static com.librarymanagement.LibraryManagement.util.security.KeycloakJwtTestSupport.patron;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, KeycloakRoleConverter.class, GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CategoryService categoryService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Access Test: no unauthenticated users can access")
    void givenNoToken_whenGetCategories_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Get mapping: Given User role, then forbidden")
    void givenPatronRole_whenGetCategories_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/categories").with(patron()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Get mapping: Given Admin role, then ok")
    void givenAdminRole_whenGetCategories_thenOk() throws Exception {
        CategoryResponseDTO category = CategoryResponseDtoTestDataBuilder.getInstance().build();
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(category.name()));

        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("Get mapping: Given Admin role when category by id, then ok")
    void givenAdminRole_whenGetCategoryById_thenOk() throws Exception {
        CategoryResponseDTO category = CategoryResponseDtoTestDataBuilder.getInstance()
                .withId(1L)
                .withName("Programming")
                .build();
        when(categoryService.getCategoryResponseById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Programming"));

        verify(categoryService).getCategoryResponseById(1L);
    }

    @Test
    @DisplayName("Get mapping: category does not exist, then not found")
    void givenAdminRole_whenCategoryDoesNotExist_thenNotFound() throws Exception {
        when(categoryService.getCategoryResponseById(99L))
                .thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(get("/api/categories/99").with(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("Post mapping: Given User role, then forbidden")
    void givenPatronRole_whenAddCategory_thenForbidden() throws Exception {
        var request = CategoryRequestDtoTestDataBuilder.getInstance().build();

        mockMvc.perform(post("/api/categories").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).saveCategory(any());
    }

    @Test
    @DisplayName("Post mapping: Given Admin role, then created")
    void givenAdminRole_whenAddCategory_thenCreated() throws Exception {
        var request = CategoryRequestDtoTestDataBuilder.getInstance().withName("Programming").build();
        var response = CategoryResponseDtoTestDataBuilder.getInstance().withName("Programming").build();

        when(categoryService.saveCategory(request)).thenReturn(response);

        mockMvc.perform(post("/api/categories").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Programming"));

        verify(categoryService).saveCategory(request);
    }

    @Test
    @DisplayName("Post mapping: blank name, then bad request")
    void givenAdminRole_whenAddCategoryWithBlankName_thenBadRequest() throws Exception {
        var request = CategoryRequestDtoTestDataBuilder.getInstance().withName("").build();

        mockMvc.perform(post("/api/categories").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());

        verify(categoryService, never()).saveCategory(any());
    }

    @Test
    @DisplayName("Put mapping: Given User role, then forbidden")
    void givenPatronRole_whenUpdateCategory_thenForbidden() throws Exception {
        var request = CategoryRequestDtoTestDataBuilder.getInstance().build();

        mockMvc.perform(put("/api/categories/1").with(patron())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).updateCategory(anyLong(), any());
    }

    @Test
    @DisplayName("Put mapping: Given Admin role, then ok")
    void givenAdminRole_whenUpdateCategory_thenOk() throws Exception {
        var request = CategoryRequestDtoTestDataBuilder.getInstance().withName("Updated").build();
        var response = CategoryResponseDtoTestDataBuilder.getInstance().withName("Updated").build();

        when(categoryService.updateCategory(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/categories/1").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(categoryService).updateCategory(1L, request);
    }

    @Test
    @DisplayName("Delete mapping: Given User role, then forbidden")
    void givenPatronRole_whenDeleteCategory_thenForbidden() throws Exception {
        mockMvc.perform(delete("/api/categories/1").with(patron()))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).deleteCategory(anyLong());
    }

    @Test
    @DisplayName("Delete mapping: Given Admin role, then no content")
    void givenAdminRole_whenDeleteCategory_thenNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1").with(admin()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    @DisplayName("Patch mapping: Given User role, then forbidden")
    void givenPatronRole_whenPatchCategory_thenForbidden() throws Exception {
        String patchBody = PatchBodyBuilder.getInstance().targetColumn("name").columnValue("x").build();

        mockMvc.perform(patch("/api/categories/1").with(patron())
                        .contentType("application/json-patch+json")
                        .content(patchBody))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).patchCategory(anyLong(), any());
    }

    @Test
    @DisplayName("Patch mapping: Given Admin role, then ok")
    void givenAdminRole_whenPatchCategory_thenOk() throws Exception {
        String patchBody = PatchBodyBuilder.getInstance().targetColumn("name").columnValue("Patched").build();
        var response = CategoryResponseDtoTestDataBuilder.getInstance().withName("Patched").build();

        when(categoryService.patchCategory(1L, patchBody)).thenReturn(response);

        mockMvc.perform(patch("/api/categories/1").with(admin())
                        .contentType("application/json-patch+json")
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patched"));

        verify(categoryService).patchCategory(1L, patchBody);
    }
}
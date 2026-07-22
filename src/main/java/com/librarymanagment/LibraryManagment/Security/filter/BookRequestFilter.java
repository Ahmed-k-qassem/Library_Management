package com.librarymanagment.LibraryManagment.Security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BookRequestFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return !uri.startsWith("/api/books");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader("Kiosk_Request_Id");
            if (requestId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        doFilter(request,response,filterChain);
    }
}

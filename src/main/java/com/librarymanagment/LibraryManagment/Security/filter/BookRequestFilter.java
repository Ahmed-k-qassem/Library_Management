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


        return !request.getServletPath().equals("/api/books");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        int requestId = request.getIntHeader("Kiosk_Request_Id");
        if(requestId <= 0){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        System.out.println(requestId);
        doFilter(request,response,filterChain);
    }
}

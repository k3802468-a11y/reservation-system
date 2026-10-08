package com.reservation.vor.reservation_system.security;

import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.security.EmployeeDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final EmployeeRepository employeeRepository;



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {



        // 取得 Authorization Header

        final String authHeader =
                request.getHeader("Authorization");



        // 沒有 Token 直接往下走

        if(authHeader == null ||
                !authHeader.startsWith("Bearer ")){

            filterChain.doFilter(request,response);

            return;

        }



        // 移除 Bearer

        String token =
                authHeader.substring(7);



        String username;


        try {

            username =
                    jwtService.extractUsername(token);


        } catch(Exception e){

            filterChain.doFilter(request,response);

            return;

        }



        // 尚未登入

        if(username != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null){



            var employee =
                    employeeRepository
                            .findByUsername(username)
                            .orElse(null);



            if(employee != null &&
                    jwtService.isValid(token)){



                EmployeeDetails employeeDetails =
                        new EmployeeDetails(employee);


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                employeeDetails,
                                null,
                                employeeDetails.getAuthorities()
                        );



                authentication
                        .setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );



                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            }

        }



        filterChain.doFilter(request,response);

    }

}
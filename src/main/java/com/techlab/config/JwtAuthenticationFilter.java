package com.techlab.config;

import com.techlab.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro para autenticação JWT nas requisições REST
 * Intercepta requisições para /api/** e valida o token JWT
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Aplicar filtro apenas para rotas /api/**
        String requestPath = request.getRequestURI();
        if (!requestPath.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Permitir /api/auth/login sem token
        if (requestPath.equals("/api/auth/login") && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrair token do header Authorization
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token JWT não encontrado na requisição: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token JWT não fornecido ou inválido\"}");
            return;
        }

        try {
            // Extrair token (remover "Bearer ")
            final String jwt = authHeader.substring(7);
            
            // Validar token
            if (!jwtService.validateToken(jwt)) {
                log.warn("Token JWT inválido ou expirado na requisição: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token JWT inválido ou expirado\"}");
                return;
            }

            // Extrair email do token
            final String userEmail = jwtService.extractEmail(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Criar autenticação e adicionar ao SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("Token JWT válido para usuário: {}", userEmail);
            }

        } catch (Exception e) {
            log.error("Erro ao processar token JWT", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Erro ao processar token JWT\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}


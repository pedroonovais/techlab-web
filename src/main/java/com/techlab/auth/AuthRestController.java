package com.techlab.auth;

import com.techlab.auth.dto.LoginRequest;
import com.techlab.auth.dto.LoginResponse;
import com.techlab.user.User;
import com.techlab.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para autenticação via API
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Endpoint de login que retorna token JWT
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Tentativa de login via API para: {}", request.getEmail());

            // Buscar usuário por email
            User user = userService.findByEmail(request.getEmail());
            
            if (user == null) {
                log.warn("Usuário não encontrado: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciais inválidas");
            }

            // Verificar se o usuário tem senha (usuários OAuth podem não ter)
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                log.warn("Usuário sem senha cadastrada: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciais inválidas");
            }

            // Validar senha
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Senha inválida para: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciais inválidas");
            }

            // Gerar token JWT
            String token = jwtService.generateToken(user.getEmail());
            log.info("Login bem-sucedido via API para: {}", request.getEmail());

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (Exception e) {
            log.error("Erro ao processar login via API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }
}


package com.techlab.iot;

import com.techlab.auth.dto.IotUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações com dispositivos IoT
 */
@RestController
@RequestMapping("/api/iot")
@RequiredArgsConstructor
@Slf4j
public class IotRestController {

    private final IotService iotService;

    /**
     * Atualiza um dispositivo IoT existente
     * PUT /api/iot/{id}
     * Requer autenticação JWT
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIot(
            @PathVariable Long id,
            @Valid @RequestBody IotUpdateRequest request) {
        try {
            log.info("Tentativa de atualização do IoT ID: {}", id);

            // Buscar IoT existente
            Iot iot = iotService.findById(id);

            // Atualizar campos
            if (request.getAtivo() != null) {
                iot.setAtivo(request.getAtivo());
            }
            if (request.getBateria() != null) {
                iot.setBateria(request.getBateria());
            }
            if (request.getCoordenadaX() != null && !request.getCoordenadaX().isEmpty()) {
                iot.setCoordenadaX(request.getCoordenadaX());
            }
            if (request.getCoordenadaY() != null && !request.getCoordenadaY().isEmpty()) {
                iot.setCoordenadaY(request.getCoordenadaY());
            }

            // Salvar alterações
            Iot updatedIot = iotService.save(iot);
            log.info("IoT ID {} atualizado com sucesso", id);

            return ResponseEntity.ok(updatedIot);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                log.warn("IoT não encontrado: ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"IoT não encontrado\"}");
            }
            log.error("Erro ao atualizar IoT ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erro interno do servidor\"}");
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar IoT ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erro interno do servidor\"}");
        }
    }
}


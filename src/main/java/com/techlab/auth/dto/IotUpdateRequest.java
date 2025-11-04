package com.techlab.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de dispositivo IoT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotUpdateRequest {

    private Boolean ativo;

    @Min(0)
    @Max(100)
    private Integer bateria;

    @NotBlank(message = "A coordenada X é obrigatória")
    private String coordenadaX;

    @NotBlank(message = "A coordenada Y é obrigatória")
    private String coordenadaY;
}


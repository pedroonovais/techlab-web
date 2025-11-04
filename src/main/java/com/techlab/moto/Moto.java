package com.techlab.moto;

import com.techlab.iot.Iot;
import com.techlab.patio.Patio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O modelo da moto é obrigatório.")
    private String modelo;

    @NotBlank(message = "A placa da moto é obrigatória.")
    @Column(unique = true)
    private String placa;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    // Relacionamento Many-to-One com Iot (uma moto pode ter um dispositivo IoT associado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iot_id", nullable = true)
    private Iot iot;

    // Relacionamento Many-to-One com Patio (uma moto está em um pátio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patio_id", nullable = true)
    private Patio patio;
}

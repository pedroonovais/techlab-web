package com.techlab.moto;

import com.techlab.iot.IotRepository;
import com.techlab.patio.PatioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository motoRepository;
    private final IotRepository iotRepository;
    private final PatioRepository patioRepository;

    public List<MotoDTO> findAll() {
        return motoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MotoDTO findById(Long id) {
        return motoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Moto não encontrada"));
    }

    public MotoDTO save(MotoDTO dto) {
        Moto entity = toEntity(dto);
        // Se for criação, validar placa única
        if (entity.getId() == null && motoRepository.existsByPlaca(entity.getPlaca())) {
            throw new RuntimeException("Já existe uma moto com a placa informada");
        }
        // Se for edição, validar duplicidade de placa em outro registro
        if (entity.getId() != null) {
            motoRepository.findByPlaca(entity.getPlaca())
                    .filter(existing -> !existing.getId().equals(entity.getId()))
                    .ifPresent(m -> { throw new RuntimeException("Já existe outra moto com essa placa"); });
        }
        Moto saved = motoRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        motoRepository.deleteById(id);
    }

    /* ---------- Mapping ---------- */

    private MotoDTO toDTO(Moto m) {
        return MotoDTO.builder()
                .id(m.getId())
                .modelo(m.getModelo())
                .placa(m.getPlaca())
                .dataEntrada(m.getDataEntrada())
                .dataSaida(m.getDataSaida())
                .iotId(m.getIot() != null ? m.getIot().getId() : null)
                .patioId(m.getPatio() != null ? m.getPatio().getId() : null)
                .build();
    }

    private Moto toEntity(MotoDTO dto) {
        Moto.MotoBuilder builder = Moto.builder()
                .id(dto.getId())
                .modelo(dto.getModelo())
                .placa(dto.getPlaca())
                .dataEntrada(dto.getDataEntrada())
                .dataSaida(dto.getDataSaida());

        // Mapear relacionamento com IoT se fornecido
        if (dto.getIotId() != null) {
            iotRepository.findById(dto.getIotId())
                    .ifPresentOrElse(
                            builder::iot,
                            () -> {
                                throw new RuntimeException("Dispositivo IoT não encontrado com ID: " + dto.getIotId());
                            }
                    );
        }

        // Mapear relacionamento com Pátio se fornecido
        if (dto.getPatioId() != null) {
            patioRepository.findById(dto.getPatioId())
                    .ifPresentOrElse(
                            builder::patio,
                            () -> {
                                throw new RuntimeException("Pátio não encontrado com ID: " + dto.getPatioId());
                            }
                    );
        }

        return builder.build();
    }
}

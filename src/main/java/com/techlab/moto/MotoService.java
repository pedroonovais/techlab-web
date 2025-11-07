package com.techlab.moto;

import com.techlab.iot.IotRepository;
import com.techlab.patio.PatioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository motoRepository;
    private final IotRepository iotRepository;
    private final PatioRepository patioRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional
    public MotoDTO save(MotoDTO dto) {
        // Se for criação (id == null), usar a procedure PKG_MOTO.cadastrar_moto
        if (dto.getId() == null) {
            return cadastrarMotoViaProcedure(dto);
        }
        
        // Se for edição, usar o método atual do JPA
        Moto entity = toEntity(dto);
        // Validar duplicidade de placa em outro registro
        motoRepository.findByPlaca(entity.getPlaca())
                .filter(existing -> !existing.getId().equals(entity.getId()))
                .ifPresent(m -> { throw new RuntimeException("Já existe outra moto com essa placa"); });
        
        Moto saved = motoRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * Cadastra uma nova moto usando a procedure PKG_MOTO.cadastrar_moto
     * @param dto DTO com os dados da moto a ser cadastrada
     * @return DTO da moto cadastrada com o ID gerado
     */
    private MotoDTO cadastrarMotoViaProcedure(MotoDTO dto) {
        log.info("Cadastrando moto via procedure PKG_MOTO.cadastrar_moto - Modelo: {}, Placa: {}", 
                dto.getModelo(), dto.getPlaca());
        
        if (motoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RuntimeException("Já existe uma moto com a placa informada");
        }
        
        try {
            
            Session session = entityManager.unwrap(Session.class);
            
            String sql = "{ call PKG_MOTO.cadastrar_moto(?, ?, ?, ?, ?, ?) }";
            
            session.doWork(connection -> {
                try (CallableStatement stmt = connection.prepareCall(sql)) {
                    // Converter LocalDateTime para Timestamp para o Oracle
                    java.sql.Timestamp dataEntrada = dto.getDataEntrada() != null 
                            ? java.sql.Timestamp.valueOf(dto.getDataEntrada()) 
                            : null;
                    java.sql.Timestamp dataSaida = dto.getDataSaida() != null 
                            ? java.sql.Timestamp.valueOf(dto.getDataSaida()) 
                            : null;
                    
                    stmt.setString(1, dto.getModelo());
                    stmt.setString(2, dto.getPlaca());
                    stmt.setTimestamp(3, dataEntrada);
                    
                    if (dataSaida != null) {
                        stmt.setTimestamp(4, dataSaida);
                    } else {
                        stmt.setNull(4, Types.TIMESTAMP);
                    }
                    
                    if (dto.getIotId() != null) {
                        stmt.setLong(5, dto.getIotId());
                    } else {
                        stmt.setNull(5, Types.NUMERIC);
                    }
                    
                    if (dto.getPatioId() != null) {
                        stmt.setLong(6, dto.getPatioId());
                    } else {
                        stmt.setNull(6, Types.NUMERIC);
                    }
                    
                    stmt.execute();
                    
                    log.info("Procedure executada com sucesso. Buscando moto recém-criada pela placa: {}", dto.getPlaca());
                } catch (java.sql.SQLException e) {
                    log.error("Erro ao executar procedure PKG_MOTO.cadastrar_moto", e);
                    
                    int errorCode = e.getErrorCode();
                    
                    if (errorCode == 1) {
                        throw new RuntimeException("Já existe uma moto com a placa informada");
                    }
                    
                    if (errorCode == 6502) {
                        throw new RuntimeException("Erro: Valor inválido ao cadastrar a moto");
                    }
                    
                    throw new RuntimeException("Erro inesperado ao cadastrar moto: " + e.getMessage(), e);
                }
            });
            
            Moto motoCriada = motoRepository.findByPlaca(dto.getPlaca())
                    .orElseThrow(() -> new RuntimeException("Erro ao buscar moto recém-cadastrada"));
            
            log.info("Moto cadastrada com sucesso via procedure - ID: {}, Modelo: {}, Placa: {}", 
                    motoCriada.getId(), motoCriada.getModelo(), motoCriada.getPlaca());
            
            return toDTO(motoCriada);
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao cadastrar moto via procedure", e);
            throw new RuntimeException("Erro inesperado ao cadastrar moto: " + e.getMessage(), e);
        }
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

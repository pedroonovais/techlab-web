-- Inserção de dados iniciais de pátios
INSERT INTO patio (nome) VALUES ('Pátio Centro');
INSERT INTO patio (nome) VALUES ('Pátio Norte');
INSERT INTO patio (nome) VALUES ('Pátio Sul');
INSERT INTO patio (nome) VALUES ('Pátio Leste');
INSERT INTO patio (nome) VALUES ('Pátio Oeste');
INSERT INTO patio (nome) VALUES ('Pátio Industrial');
INSERT INTO patio (nome) VALUES ('Pátio Logística 1');
INSERT INTO patio (nome) VALUES ('Pátio Logística 2');
INSERT INTO patio (nome) VALUES ('Pátio Aeroporto');
INSERT INTO patio (nome) VALUES ('Pátio Porto');

-- Inserção de dados iniciais de dispositivos IoT
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 95, 'X-23.5505', 'Y-46.6333');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 72, 'X-23.5610', 'Y-46.6561');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (0, 40, 'X-23.5702', 'Y-46.6500');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 88, 'X-23.5599', 'Y-46.6401');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (0, 15, 'X-23.5750', 'Y-46.6200');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 63, 'X-23.5601', 'Y-46.6302');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 100, 'X-23.5900', 'Y-46.6100');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (0, 5, 'X-23.6000', 'Y-46.7000');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, 54, 'X-23.5800', 'Y-46.6900');
INSERT INTO iot (ativo, bateria, coordenada_x, coordenada_y) VALUES (1, NULL, 'X-23.5650', 'Y-46.6800');

-- Inserção de dados iniciais de motos com relacionamentos
-- Moto 1 - Honda CG 160 com IoT 1 e Pátio Centro (id=1)
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Honda CG 160', 'ABC1D23', TIMESTAMP '2025-08-01 09:00:00', TIMESTAMP '2025-08-06 17:30:00', 1, 1);

-- Moto 2 - Yamaha Fazer 250 com IoT 2 e Pátio Norte (id=2)
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Yamaha Fazer 250', 'BCD2E34', TIMESTAMP '2025-08-03 10:15:00', TIMESTAMP '2025-08-13 16:45:00', 2, 2);

-- Moto 3 - Honda Biz 125 com IoT 3 e Pátio Sul (id=3)
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Honda Biz 125', 'CDE3F45', TIMESTAMP '2025-08-05 08:30:00', TIMESTAMP '2025-08-09 14:20:00', 3, 3);

-- Moto 4 - Yamaha NMax 160 com IoT 4 e Pátio Leste (id=4), ainda no pátio
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Yamaha NMax 160', 'DEF4G56', TIMESTAMP '2025-08-07 11:00:00', NULL, 4, 4);

-- Moto 5 - Honda PCX 160 com IoT 5 e Pátio Oeste (id=5)
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Honda PCX 160', 'EFG5H67', TIMESTAMP '2025-08-09 13:10:00', TIMESTAMP '2025-08-20 18:00:00', 5, 5);

-- Moto 6 - Yamaha MT-03 com IoT 6 e Pátio Industrial (id=6), ainda no pátio
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Yamaha MT-03', 'FGH6I78', TIMESTAMP '2025-08-12 09:40:00', NULL, 6, 6);

-- Moto 7 - Honda CB 500F com IoT 7 e Pátio Logística 1 (id=7)
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Honda CB 500F', 'GHI7J89', TIMESTAMP '2025-08-15 07:55:00', TIMESTAMP '2025-08-24 19:30:00', 7, 7);

-- Moto 8 - Yamaha Lander 250 com IoT 8 e Pátio Logística 2 (id=8), ainda no pátio
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Yamaha Lander 250', 'HIJ8K90', TIMESTAMP '2025-08-18 12:25:00', NULL, 8, 8);

-- Moto 9 - Honda XRE 300 com IoT 9 e Pátio Aeroporto (id=9), ainda no pátio
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Honda XRE 300', 'IJK9L01', TIMESTAMP '2025-08-22 10:05:00', NULL, 9, 9);

-- Moto 10 - Yamaha Crosser 150 com IoT 10 e Pátio Porto (id=10), ainda no pátio
INSERT INTO moto (modelo, placa, data_entrada, data_saida, iot_id, patio_id) VALUES 
    ('Yamaha Crosser 150', 'JKL0M12', TIMESTAMP '2025-08-26 15:45:00', NULL, 10, 10);


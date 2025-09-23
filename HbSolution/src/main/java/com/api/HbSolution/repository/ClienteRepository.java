package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import com.api.HbSolution.entity.ClienteEntity;

public interface ClienteRepository extends BaseRepository<ClienteEntity> {

    // Busca todos clientes ativos de uma empresa
    List<ClienteEntity> findAllByEmpresaIdAndAtivoTrue(Long empresaId);

    // Busca cliente ativo por id e empresaId
    Optional<ClienteEntity> findByIdAndEmpresaIdAndAtivoTrue(Long id, Long empresaId);
}

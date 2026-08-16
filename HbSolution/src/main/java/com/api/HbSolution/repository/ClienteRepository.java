package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.enums.StatusAtivo;

public interface ClienteRepository extends BaseRepository<ClienteEntity> {

    // Busca todos clientes ativos de uma empresa
    List<ClienteEntity> findAllByEmpresaIdAndAtivo(Long empresaId, StatusAtivo ativo);

    // Busca cliente ativo por id e empresaId
    Optional<ClienteEntity> findByIdAndEmpresaIdAndAtivo(Long id, Long empresaId, StatusAtivo ativo);

    Optional<ClienteEntity> findByEmpresaIdAndEmailAndAtivo(Long empresaId, String email, StatusAtivo ativo);

    Optional<ClienteEntity> findByEmpresaIdAndTelefoneAndAtivo(Long empresaId, String telefone, StatusAtivo ativo);
}

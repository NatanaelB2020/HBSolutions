package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.entity.EmpresaEntity;

public interface ClienteRepository extends BaseRepository<ClienteEntity> {
    List<ClienteEntity> findAllByEmpresa(EmpresaEntity empresa);
    Optional<ClienteEntity> findByIdAndEmpresa(Long id, EmpresaEntity empresa);
}

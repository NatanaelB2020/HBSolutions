package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.enums.StatusAtivo;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    // Busca todas as entidades com status ativo de uma empresa
    List<T> findAllByEmpresaIdAndAtivo(Long empresaId, StatusAtivo ativo);

    // Busca entidade por id, empresa e status ativo
    Optional<T> findByIdAndEmpresaIdAndAtivo(Long id, Long empresaId, StatusAtivo ativo);
}

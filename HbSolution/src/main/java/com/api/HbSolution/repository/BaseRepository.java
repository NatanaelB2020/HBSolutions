package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.api.HbSolution.entity.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    // Busca todas as entidades ativas de uma empresa
    List<T> findAllByEmpresaIdAndAtivoTrue(Long empresaId);

    // Busca entidade ativa por id e empresa
    Optional<T> findByIdAndEmpresaIdAndAtivoTrue(Long id, Long empresaId);
}

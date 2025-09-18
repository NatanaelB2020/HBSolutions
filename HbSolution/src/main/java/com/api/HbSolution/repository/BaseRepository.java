package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.api.HbSolution.entity.BaseEntity;
import com.api.HbSolution.entity.EmpresaEntity;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    List<T> findAllByEmpresa(EmpresaEntity empresa);
    Optional<T> findByIdAndEmpresa(Long id, EmpresaEntity empresa);
}

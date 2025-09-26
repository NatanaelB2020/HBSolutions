package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.api.HbSolution.entity.EmpresaEntity;
import com.api.HbSolution.enums.StatusAtivo;

@Repository
public interface EmpresaRepository extends BaseRepository<EmpresaEntity> {

    // Busca empresas ativas que contenham parte do nome
    List<EmpresaEntity> findAllByNomeContainingAndAtivo(String nome, StatusAtivo status);

    // Verifica se já existe empresa ativa com determinado CNPJ
    Optional<EmpresaEntity> findByCnpjAndAtivo(String cnpj, StatusAtivo status);
    
}
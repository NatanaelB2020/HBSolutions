package com.api.HbSolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.HbSolution.entity.AtendimentoMesaEntity;

@Repository
public interface AtendimentoMesaRepository extends JpaRepository<AtendimentoMesaEntity, Long> {

}

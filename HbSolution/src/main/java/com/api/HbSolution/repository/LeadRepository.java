package com.api.HbSolution.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusLead;

@Repository
public interface LeadRepository extends BaseRepository<LeadEntity> {

    List<LeadEntity> findAllByEmpresaId(Long empresaId);

    List<LeadEntity> findAllByEmpresaIdAndAtivo(Long empresaId, StatusAtivo ativo);

    List<LeadEntity> findAllByEmpresaIdAndCreatedAtBetween(Long empresaId, java.time.LocalDateTime inicio,
            java.time.LocalDateTime fim);

    Optional<LeadEntity> findByEmpresaIdAndEmailAndAtivo(Long empresaId, String email, StatusAtivo ativo);

    Optional<LeadEntity> findByEmpresaIdAndTelefoneAndAtivo(Long empresaId, String telefone, StatusAtivo ativo);

    @Query("SELECT l FROM LeadEntity l WHERE l.empresaId = :empresaId AND l.ativo = :ativo " +
            "AND (:nome IS NULL OR LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:telefone IS NULL OR l.telefone LIKE CONCAT('%', :telefone, '%')) " +
            "AND (:status IS NULL OR l.status = :status) " +
            "AND (:origem IS NULL OR l.origem = :origem) " +
            "AND (:scoreMinimo IS NULL OR l.score >= :scoreMinimo) " +
            "ORDER BY l.score DESC, l.createdAt DESC")
    Page<LeadEntity> buscarLeadsFiltrados(@Param("empresaId") Long empresaId,
            @Param("ativo") StatusAtivo ativo,
            @Param("nome") String nome,
            @Param("telefone") String telefone,
            @Param("status") StatusLead status,
            @Param("origem") OrigemLead origem,
            @Param("scoreMinimo") Integer scoreMinimo,
            Pageable pageable);
}

package com.api.HbSolution.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusAtividade;

@Repository
public interface AtividadeRepository extends BaseRepository<AtividadeEntity> {

    List<AtividadeEntity> findAllByEmpresaId(Long empresaId);

    List<AtividadeEntity> findAllByEmpresaIdAndAtivo(Long empresaId, StatusAtivo ativo);

    List<AtividadeEntity> findAllByOportunidadeId(Long oportunidadeId);

    List<AtividadeEntity> findAllByUsuarioResponsavelIdAndStatusAndAtivo(Long usuarioId, StatusAtividade status,
            StatusAtivo ativo);

    List<AtividadeEntity> findByUsuarioResponsavelIdAndStatusAndDataAgendamentoBetween(Long usuarioId,
            StatusAtividade status, LocalDateTime inicio, LocalDateTime fim);

    List<AtividadeEntity> findByUsuarioResponsavelIdAndStatusAndDataAgendamentoBefore(Long usuarioId,
            StatusAtividade status, LocalDateTime dataLimite);

    AtividadeEntity findTopByOportunidadeIdOrderByDataAtividadeDesc(Long oportunidadeId);
}

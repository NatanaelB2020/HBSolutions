package com.api.HbSolution.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusOportunidade;

@Repository
public interface OportunidadeRepository extends BaseRepository<OportunidadeEntity> {

    List<OportunidadeEntity> findAllByEmpresaId(Long empresaId);

    List<OportunidadeEntity> findAllByEmpresaIdAndAtivo(Long empresaId, StatusAtivo ativo);

    List<OportunidadeEntity> findAllByEtapaAndEmpresaIdAndAtivo(EtapaOportunidade etapa, Long empresaId,
            StatusAtivo ativo);

    List<OportunidadeEntity> findAllByStatusAndEmpresaIdAndAtivo(StatusOportunidade status, Long empresaId,
            StatusAtivo ativo);

    Long countByUsuarioResponsavelIdAndStatus(Long usuarioResponsavelId, StatusOportunidade status);

    List<OportunidadeEntity> findAllByEmpresaIdAndStatusAndDataFechamentoRealBetween(Long empresaId,
            StatusOportunidade status, LocalDate dataInicio, LocalDate dataFim);
}

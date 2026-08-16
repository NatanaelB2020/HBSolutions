package com.api.HbSolution.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.HbSolution.DTO.RealizarAtividadeRequest;
import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusAtividade;
import com.api.HbSolution.enums.TipoAtividade;
import com.api.HbSolution.repository.AtividadeRepository;
import com.api.HbSolution.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class AtividadeService extends BaseService<AtividadeEntity> {

    private final AtividadeRepository atividadeRepository;

    @Autowired
    public AtividadeService(AtividadeRepository atividadeRepository) {
        super.setRepository(atividadeRepository);
        this.atividadeRepository = atividadeRepository;
    }

    public List<AtividadeEntity> findByEmpresaId(Long empresaId) {
        return atividadeRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<AtividadeEntity> findAtivosByEmpresaId(Long empresaId) {
        return atividadeRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<AtividadeEntity> findByOportunidadeId(Long oportunidadeId) {
        return atividadeRepository.findAllByOportunidadeId(oportunidadeId);
    }

    public List<AtividadeEntity> findPendentesByUsuarioId(Long usuarioId) {
        return atividadeRepository.findAllByUsuarioResponsavelIdAndStatusAndAtivo(usuarioId, StatusAtividade.PENDENTE,
                StatusAtivo.ATIVO);
    }

    public List<AtividadeEntity> findPendentesHojeByUsuarioId(Long usuarioId, LocalDateTime inicioDoDia,
            LocalDateTime fimDoDia) {
        return atividadeRepository.findByUsuarioResponsavelIdAndStatusAndDataAgendamentoBetween(usuarioId,
                StatusAtividade.PENDENTE, inicioDoDia, fimDoDia);
    }

    public List<AtividadeEntity> findPendentesByUsuarioIdAndHoje(Long usuarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(hoje, LocalTime.MAX);
        return findPendentesHojeByUsuarioId(usuarioId, inicioDoDia, fimDoDia);
    }

    public List<AtividadeEntity> findPendentesAtrasadasByUsuarioId(Long usuarioId, LocalDateTime dataLimite) {
        return atividadeRepository.findByUsuarioResponsavelIdAndStatusAndDataAgendamentoBefore(usuarioId,
                StatusAtividade.PENDENTE, dataLimite);
    }

    public List<AtividadeEntity> findPendentesAtrasadasByUsuarioId(Long usuarioId) {
        return findPendentesAtrasadasByUsuarioId(usuarioId, LocalDateTime.now());
    }

    @Transactional
    public AtividadeEntity realizarAtividade(Long atividadeId, RealizarAtividadeRequest request) {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();
        AtividadeEntity atividade = atividadeRepository
                .findByIdAndEmpresaIdAndAtivo(atividadeId, empresaId, StatusAtivo.ATIVO)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada para a empresa do usuário"));

        if (atividade.getStatus() == StatusAtividade.REALIZADA || atividade.getStatus() == StatusAtividade.CANCELADA) {
            throw new IllegalStateException("Atividade já foi realizada ou cancelada");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request de atividade é obrigatória");
        }

        if (request.getResultado() == null || request.getResultado().isBlank()) {
            throw new IllegalArgumentException("Resultado da atividade é obrigatório");
        }

        atividade.setResultado(request.getResultado());
        atividade.setDuracaoMinutos(request.getDuracaoMinutos());
        atividade.setStatus(request.getStatus() != null ? request.getStatus() : StatusAtividade.REALIZADA);
        atividade.setDataAtividade(LocalDateTime.now());

        OportunidadeEntity oportunidade = atividade.getOportunidade();
        if (request.getProximaEtapaOportunidade() != null && oportunidade != null) {
            oportunidade.setEtapa(request.getProximaEtapaOportunidade());
            oportunidade.setAlertaAtivo(false);
        }

        if (request.getProximaAtividadeTitulo() != null && !request.getProximaAtividadeTitulo().isBlank()) {
            UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
            AtividadeEntity proximaAtividade = new AtividadeEntity();
            proximaAtividade.setTitulo(request.getProximaAtividadeTitulo());
            proximaAtividade.setTipo(TipoAtividade.TAREFA);
            proximaAtividade.setDescricao("Follow-up agendado após atividade realizada");
            proximaAtividade.setDataAgendamento(request.getProximaAtividadeData() != null
                    ? request.getProximaAtividadeData()
                    : LocalDateTime.now().plusDays(1));
            proximaAtividade.setStatus(StatusAtividade.PENDENTE);
            proximaAtividade.setOportunidade(oportunidade);
            proximaAtividade.setUsuarioResponsavel(usuarioLogado);
            proximaAtividade.setEmpresaId(empresaId);
            proximaAtividade.setUsuarioId(usuarioLogado.getId());
            proximaAtividade.setAtivo(StatusAtivo.ATIVO);
            atividadeRepository.save(proximaAtividade);
        }

        atividade.setUpdatedAt(LocalDateTime.now());
        return atividadeRepository.save(atividade);
    }
}

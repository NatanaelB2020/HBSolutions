package com.api.HbSolution.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.api.HbSolution.DTO.OportunidadeAlertaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.PedidoEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.exception.ResourceNotFoundException;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.repository.ClienteRepository;
import com.api.HbSolution.repository.OportunidadeRepository;
import com.api.HbSolution.repository.PedidoRepository;
import com.api.HbSolution.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class OportunidadeService extends BaseService<OportunidadeEntity> {

    private final OportunidadeRepository oportunidadeRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public OportunidadeService(OportunidadeRepository oportunidadeRepository, ClienteRepository clienteRepository,
            PedidoRepository pedidoRepository) {
        super.setRepository(oportunidadeRepository);
        this.oportunidadeRepository = oportunidadeRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<OportunidadeEntity> findByEmpresaId(Long empresaId) {
        return oportunidadeRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<OportunidadeEntity> findAllByEmpresaId(Long empresaId) {
        return oportunidadeRepository.findAllByEmpresaId(empresaId);
    }

    public List<OportunidadeEntity> findAtivosByEmpresaId(Long empresaId) {
        return oportunidadeRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<OportunidadeEntity> findByEtapa(EtapaOportunidade etapa) {
        return oportunidadeRepository.findAllByEtapaAndEmpresaIdAndAtivo(etapa, getEmpresaIdAtual(), StatusAtivo.ATIVO);
    }

    public List<OportunidadeEntity> findByStatus(StatusOportunidade status) {
        return oportunidadeRepository.findAllByStatusAndEmpresaIdAndAtivo(status, getEmpresaIdAtual(),
                StatusAtivo.ATIVO);
    }

    public List<OportunidadeAlertaDTO> findAlertasByEmpresaId(Long empresaId) {
        List<OportunidadeEntity> oportunidades = findByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.ABERTA || o.getStatus() == StatusOportunidade.PAUSADA)
                .toList();

        List<OportunidadeAlertaDTO> alertas = new ArrayList<>();

        for (OportunidadeEntity oportunidade : oportunidades) {
            LocalDateTime referencia = oportunidade.getUpdatedAt() != null ? oportunidade.getUpdatedAt()
                    : oportunidade.getCreatedAt();

            if (referencia == null) {
                continue;
            }

            long diasParada = java.time.temporal.ChronoUnit.DAYS.between(referencia.toLocalDate(), LocalDate.now());
            if (diasParada >= 5) {
                OportunidadeAlertaDTO dto = new OportunidadeAlertaDTO();
                dto.setOportunidadeId(oportunidade.getId());
                dto.setTitulo(oportunidade.getTitulo());
                dto.setEtapa(oportunidade.getEtapa() != null ? oportunidade.getEtapa().name() : null);
                dto.setValor(oportunidade.getValor());
                dto.setDiasParada((int) diasParada);
                dto.setUltimaAtividadeTitulo("Sem atividade");
                dto.setDataUltimaAtividade(referencia);
                alertas.add(dto);
            }
        }

        alertas.sort(Comparator.comparingInt(OportunidadeAlertaDTO::getDiasParada).reversed());
        return alertas;
    }

    @Transactional
    public OportunidadeEntity moverEtapa(Long oportunidadeId, EtapaOportunidade novaEtapa, String motivo) {
        Long empresaIdAtual = getEmpresaIdAtual();
        if (empresaIdAtual == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        OportunidadeEntity oportunidade = oportunidadeRepository
                .findByIdAndEmpresaIdAndAtivo(oportunidadeId, empresaIdAtual, StatusAtivo.ATIVO)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Oportunidade não encontrada para a empresa do usuário"));

        if (oportunidade.getStatus() == StatusOportunidade.GANHA
                || oportunidade.getStatus() == StatusOportunidade.PERDIDA) {
            throw new IllegalStateException("Oportunidade ja fechada");
        }

        EtapaOportunidade etapaAnterior = oportunidade.getEtapa();
        boolean etapaAnteriorFechamento = etapaAnterior == EtapaOportunidade.FECHAMENTO_GANHO
                || etapaAnterior == EtapaOportunidade.FECHAMENTO_PERDIDO;

        if (novaEtapa == EtapaOportunidade.FECHAMENTO_GANHO) {
            if (oportunidade.getValor() == null) {
                throw new IllegalStateException("Informe o valor antes de ganhar");
            }
            oportunidade.setStatus(StatusOportunidade.GANHA);
            oportunidade.setDataFechamentoReal(LocalDate.now());
            oportunidade.setMotivoPerda(null);
            oportunidade.setEtapa(novaEtapa);
            return save(oportunidade);
        }

        if (novaEtapa == EtapaOportunidade.FECHAMENTO_PERDIDO) {
            if (motivo == null || motivo.isBlank()) {
                throw new IllegalStateException("Informe o motivo antes de perder");
            }
            oportunidade.setStatus(StatusOportunidade.PERDIDA);
            oportunidade.setMotivoPerda(motivo);
            oportunidade.setEtapa(novaEtapa);
            return save(oportunidade);
        }

        if (etapaAnteriorFechamento && novaEtapa != null) {
            throw new IllegalStateException("Oportunidade fechada nao pode reabrir por aqui");
        }

        oportunidade.setEtapa(novaEtapa);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setAlertaAtivo(false);
        return save(oportunidade);
    }

    @Transactional
    public OportunidadeEntity fecharOportunidade(Long oportunidadeId, StatusOportunidade novoStatus, String motivo,
            BigDecimal valorFinal) {
        Long empresaIdAtual = getEmpresaIdAtual();
        if (empresaIdAtual == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        OportunidadeEntity oportunidade = oportunidadeRepository
                .findByIdAndEmpresaIdAndAtivo(oportunidadeId, empresaIdAtual, StatusAtivo.ATIVO)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Oportunidade não encontrada para a empresa do usuário"));

        if (oportunidade.getStatus() == StatusOportunidade.GANHA
                || oportunidade.getStatus() == StatusOportunidade.PERDIDA) {
            throw new IllegalStateException("Oportunidade ja fechada");
        }

        if (oportunidade.getStatus() != StatusOportunidade.ABERTA
                && oportunidade.getStatus() != StatusOportunidade.PAUSADA) {
            throw new IllegalStateException("Oportunidade ja fechada");
        }

        if (novoStatus == StatusOportunidade.PERDIDA) {
            if (motivo == null || motivo.isBlank()) {
                throw new IllegalArgumentException("Motivo é obrigatório para oportunidade perdida");
            }
            oportunidade.setMotivoPerda(motivo);
            oportunidade.setEtapa(EtapaOportunidade.FECHAMENTO_PERDIDO);
        }

        if (novoStatus == StatusOportunidade.GANHA) {
            if (valorFinal == null) {
                throw new IllegalArgumentException("valorFinal é obrigatório para oportunidade ganha");
            }
            oportunidade.setValor(valorFinal);
            oportunidade.setDataFechamentoReal(LocalDate.now());
            oportunidade.setEtapa(EtapaOportunidade.FECHAMENTO_GANHO);

            UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
            ClienteEntity cliente = oportunidade.getCliente();
            if (cliente == null && oportunidade.getLead() != null) {
                var lead = oportunidade.getLead();

                if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
                    cliente = clienteRepository.findByEmpresaIdAndEmailAndAtivo(
                            oportunidade.getEmpresaId(), lead.getEmail(), StatusAtivo.ATIVO).orElse(null);
                }

                if (cliente == null && lead.getTelefone() != null && !lead.getTelefone().isBlank()) {
                    cliente = clienteRepository.findByEmpresaIdAndTelefoneAndAtivo(
                            oportunidade.getEmpresaId(), lead.getTelefone(), StatusAtivo.ATIVO).orElse(null);
                }

                if (cliente != null) {
                    System.out.println("Cliente duplicado evitado: " + cliente.getNome());
                    oportunidade.setCliente(cliente);
                } else {
                    cliente = new ClienteEntity();
                    cliente.setNome(lead.getNome());
                    cliente.setTelefone(lead.getTelefone());
                    cliente.setEmail(lead.getEmail());
                    cliente.setCpf(gerarCpfTemporario(lead.getId()));
                    cliente.setEmpresaId(oportunidade.getEmpresaId());
                    cliente.setUsuarioId(usuarioLogado.getId());
                    cliente.setAtivo(StatusAtivo.ATIVO);
                    cliente.setUsuario(usuarioLogado);
                    cliente = clienteRepository.save(cliente);
                    oportunidade.setCliente(cliente);
                }
            }

            PedidoEntity pedido = new PedidoEntity();
            pedido.setCliente(cliente);
            pedido.setOportunidade(oportunidade);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus("ABERTO");
            pedido.setValorTotal(valorFinal);
            pedido.setObservacao("Pedido gerado automaticamente pela conversão da oportunidade");
            pedidoRepository.save(pedido);
        }

        oportunidade.setStatus(novoStatus);
        oportunidade.setAlertaAtivo(false);
        return save(oportunidade);
    }

    private String gerarCpfTemporario(Long leadId) {
        String base = String.valueOf(leadId == null ? System.currentTimeMillis() : leadId);
        String digits = base.replaceAll("\\D", "");
        if (digits.length() >= 11) {
            return digits.substring(digits.length() - 11);
        }
        return String.format("%011d", Long.parseLong(digits.isBlank() ? "0" : digits));
    }

    private Long getEmpresaIdAtual() {
        var usuarioLogado = com.api.HbSolution.security.SecurityUtils.getUsuarioLogado();
        return usuarioLogado != null ? usuarioLogado.getEmpresaId() : null;
    }
}

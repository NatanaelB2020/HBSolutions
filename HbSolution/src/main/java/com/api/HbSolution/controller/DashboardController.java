package com.api.HbSolution.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.AtividadeDTO;
import com.api.HbSolution.DTO.DashboardDTO;
import com.api.HbSolution.DTO.OportunidadeAlertaDTO;
import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.AtividadeService;
import com.api.HbSolution.service.LeadService;
import com.api.HbSolution.service.OportunidadeService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AtividadeService atividadeService;
    private final OportunidadeService oportunidadeService;
    private final LeadService leadService;

    @Autowired
    public DashboardController(AtividadeService atividadeService, OportunidadeService oportunidadeService,
            LeadService leadService) {
        this.atividadeService = atividadeService;
        this.oportunidadeService = oportunidadeService;
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();
        Long usuarioId = SecurityUtils.getUsuarioLogado().getId();

        List<AtividadeEntity> atividadesHoje = atividadeService.findPendentesByUsuarioIdAndHoje(usuarioId);
        List<AtividadeEntity> atividadesAtrasadas = atividadeService.findPendentesAtrasadasByUsuarioId(usuarioId);
        List<OportunidadeAlertaDTO> oportunidadesAlerta = oportunidadeService.findAlertasByEmpresaId(empresaId);

        List<OportunidadeEntity> oportunidadesAtivas = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.ABERTA || o.getStatus() == StatusOportunidade.PAUSADA)
                .toList();

        BigDecimal valorPipeline = oportunidadesAtivas.stream()
                .map(OportunidadeEntity::getValor)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        Long quantidadeLeadsNovosHoje = leadService.findByEmpresaId(empresaId).stream()
                .filter(lead -> lead.getCreatedAt() != null && !lead.getCreatedAt().isBefore(inicioDoDia))
                .count();

        Long quantidadeOportunidadesAtivas = (long) oportunidadesAtivas.size();

        LocalDate primeiroDiaMes = hoje.withDayOfMonth(1);
        LocalDateTime inicioMes = LocalDateTime.of(primeiroDiaMes, LocalTime.MIN);
        LocalDateTime fimMes = LocalDateTime.of(hoje.withDayOfMonth(hoje.lengthOfMonth()), LocalTime.MAX);

        BigDecimal totalVendidoMes = oportunidadeService.findAllByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA)
                .filter(o -> o.getDataFechamentoReal() != null
                        && !o.getDataFechamentoReal().isBefore(primeiroDiaMes)
                        && !o.getDataFechamentoReal().isAfter(hoje))
                .map(OportunidadeEntity::getValor)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OportunidadeEntity> oportunidadesMes = oportunidadeService.findAllByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA || o.getStatus() == StatusOportunidade.PERDIDA)
                .filter(o -> o.getDataFechamentoReal() != null
                        && !o.getDataFechamentoReal().isBefore(primeiroDiaMes)
                        && !o.getDataFechamentoReal().isAfter(hoje))
                .toList();

        long ganhas = oportunidadesMes.stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA)
                .count();
        long perdidas = oportunidadesMes.stream()
                .filter(o -> o.getStatus() == StatusOportunidade.PERDIDA)
                .count();

        Double taxaConversaoMes = (ganhas + perdidas) == 0 ? 0.0
                : (ganhas * 100.0) / (ganhas + perdidas);

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setAtividadesHoje(atividadesHoje.stream().map(this::toDTO).collect(Collectors.toList()));
        dashboard.setAtividadesAtrasadas(atividadesAtrasadas.stream().map(this::toDTO).collect(Collectors.toList()));
        dashboard.setOportunidadesAlerta(oportunidadesAlerta);
        dashboard.setValorPipeline(valorPipeline);
        dashboard.setQuantidadeLeadsNovosHoje(quantidadeLeadsNovosHoje);
        dashboard.setQuantidadeOportunidadesAtivas(quantidadeOportunidadesAtivas);
        dashboard.setTotalVendidoMes(totalVendidoMes);
        dashboard.setTaxaConversaoMes(taxaConversaoMes);

        return ResponseEntity.ok(dashboard);
    }

    private AtividadeDTO toDTO(AtividadeEntity entity) {
        AtividadeDTO dto = new AtividadeDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setTipo(entity.getTipo());
        dto.setDescricao(entity.getDescricao());
        dto.setStatus(entity.getStatus());
        dto.setDataAgendamento(entity.getDataAgendamento());
        dto.setDuracaoMinutos(entity.getDuracaoMinutos());
        dto.setResultado(entity.getResultado());
        return dto;
    }
}

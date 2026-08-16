package com.api.HbSolution.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.FecharOportunidadeRequest;
import com.api.HbSolution.DTO.MoverEtapaRequest;
import com.api.HbSolution.DTO.OportunidadeAlertaDTO;
import com.api.HbSolution.DTO.OportunidadeDTO;
import com.api.HbSolution.DTO.RelatorioVendasDTO;
import com.api.HbSolution.DTO.ResumoCrmDTO;
import com.api.HbSolution.DTO.VendaPorDiaDTO;
import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.repository.AtividadeRepository;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.LeadService;
import com.api.HbSolution.service.OportunidadeService;

@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeController extends BaseController<OportunidadeEntity, OportunidadeService> {

    private final OportunidadeService oportunidadeService;
    private final LeadService leadService;
    private final AtividadeRepository atividadeRepository;

    @Autowired
    public OportunidadeController(OportunidadeService oportunidadeService, LeadService leadService,
            AtividadeRepository atividadeRepository) {
        this.oportunidadeService = oportunidadeService;
        this.leadService = leadService;
        this.atividadeRepository = atividadeRepository;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<OportunidadeEntity>> findByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(oportunidadeService.findByEmpresaId(empresaId));
    }

    @GetMapping("/ativos/empresa/{empresaId}")
    public ResponseEntity<List<OportunidadeEntity>> findAtivosByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(oportunidadeService.findAtivosByEmpresaId(empresaId));
    }

    @GetMapping("/pipeline")
    public ResponseEntity<Map<String, List<OportunidadeDTO>>> getPipeline() {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();

        List<OportunidadeEntity> oportunidades = oportunidadeService.findByEmpresaId(empresaId);

        Map<String, List<OportunidadeDTO>> pipeline = new LinkedHashMap<>();

        for (EtapaOportunidade etapa : List.of(
                EtapaOportunidade.PROSPECCAO,
                EtapaOportunidade.QUALIFICACAO,
                EtapaOportunidade.PROPOSTA_ENVIADA,
                EtapaOportunidade.NEGOCIACAO)) {
            pipeline.put(etapa.name(), new ArrayList<>());
        }

        for (OportunidadeEntity oportunidade : oportunidades) {
            if (oportunidade.getStatus() == StatusOportunidade.GANHA
                    || oportunidade.getStatus() == StatusOportunidade.PERDIDA) {
                continue;
            }

            if (oportunidade.getEtapa() == null) {
                continue;
            }

            if (pipeline.containsKey(oportunidade.getEtapa().name())) {
                pipeline.get(oportunidade.getEtapa().name()).add(toDTO(oportunidade));
            }
        }

        return ResponseEntity.ok(pipeline);
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoCrmDTO> getResumoCrm() {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();

        List<OportunidadeEntity> oportunidadesAtivas = oportunidadeService.findByEmpresaId(empresaId);
        List<OportunidadeEntity> todasAsOportunidades = oportunidadeService.findAllByEmpresaId(empresaId);

        BigDecimal valorTotalPipeline = oportunidadesAtivas.stream()
                .map(OportunidadeEntity::getValor)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = LocalDateTime.of(hoje.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime fimMes = LocalDateTime.of(hoje.withDayOfMonth(hoje.lengthOfMonth()), LocalTime.MAX);

        BigDecimal valorTotalGanhoMes = todasAsOportunidades.stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA)
                .filter(o -> o.getUpdatedAt() != null && !o.getUpdatedAt().isBefore(inicioMes)
                        && !o.getUpdatedAt().isAfter(fimMes))
                .map(OportunidadeEntity::getValor)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalPerdidoMes = todasAsOportunidades.stream()
                .filter(o -> o.getStatus() == StatusOportunidade.PERDIDA)
                .filter(o -> o.getUpdatedAt() != null && !o.getUpdatedAt().isBefore(inicioMes)
                        && !o.getUpdatedAt().isAfter(fimMes))
                .map(OportunidadeEntity::getValor)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long quantidadeOportunidadesAtivas = (long) oportunidadesAtivas.size();
        Long quantidadeLeadsNovosMes = (long) leadService.findNovosNoMes(empresaId).size();

        long totalFechadas = todasAsOportunidades.stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA || o.getStatus() == StatusOportunidade.PERDIDA)
                .count();

        Double taxaConversao = totalFechadas == 0 ? 0.0
                : (double) todasAsOportunidades.stream()
                        .filter(o -> o.getStatus() == StatusOportunidade.GANHA)
                        .count() * 100.0 / totalFechadas;

        ResumoCrmDTO resumo = new ResumoCrmDTO();
        resumo.setValorTotalPipeline(valorTotalPipeline);
        resumo.setValorTotalGanhoMes(valorTotalGanhoMes);
        resumo.setValorTotalPerdidoMes(valorTotalPerdidoMes);
        resumo.setQuantidadeOportunidadesAtivas(quantidadeOportunidadesAtivas);
        resumo.setQuantidadeLeadsNovosMes(quantidadeLeadsNovosMes);
        resumo.setTaxaConversao(taxaConversao);

        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<OportunidadeAlertaDTO>> getOportunidadesParadas() {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();
        LocalDate hoje = LocalDate.now();

        List<OportunidadeEntity> oportunidades = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.ABERTA)
                .toList();

        List<OportunidadeAlertaDTO> alertas = new ArrayList<>();

        for (OportunidadeEntity oportunidade : oportunidades) {
            AtividadeEntity ultimaAtividade = atividadeRepository.findTopByOportunidadeIdOrderByDataAtividadeDesc(
                    oportunidade.getId());

            LocalDateTime referencia = ultimaAtividade != null ? ultimaAtividade.getDataAtividade()
                    : oportunidade.getCreatedAt();
            if (referencia == null) {
                referencia = oportunidade.getUpdatedAt();
            }

            long diasParada = ChronoUnit.DAYS.between(referencia.toLocalDate(), hoje);
            if (diasParada >= 5) {
                OportunidadeAlertaDTO dto = new OportunidadeAlertaDTO();
                dto.setOportunidadeId(oportunidade.getId());
                dto.setTitulo(oportunidade.getTitulo());
                dto.setEtapa(oportunidade.getEtapa() != null ? oportunidade.getEtapa().name() : null);
                dto.setValor(oportunidade.getValor());
                dto.setDiasParada((int) diasParada);
                dto.setUltimaAtividadeTitulo(ultimaAtividade != null ? ultimaAtividade.getTitulo() : "Sem atividade");
                dto.setDataUltimaAtividade(referencia);
                alertas.add(dto);
            }
        }

        alertas.sort(Comparator.comparingInt(OportunidadeAlertaDTO::getDiasParada).reversed());
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/etapa/{etapa}")
    public ResponseEntity<List<OportunidadeEntity>> findByEtapa(@PathVariable EtapaOportunidade etapa) {
        return ResponseEntity.ok(oportunidadeService.findByEtapa(etapa));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OportunidadeEntity>> findByStatus(@PathVariable StatusOportunidade status) {
        return ResponseEntity.ok(oportunidadeService.findByStatus(status));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<OportunidadeDTO>> getMinhasOportunidades() {
        var usuarioLogado = SecurityUtils.getUsuarioLogado();
        Long empresaId = usuarioLogado.getEmpresaId();
        Long usuarioId = usuarioLogado.getId();

        List<OportunidadeEntity> oportunidades = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(oportunidade -> isAdminOrOwner(usuarioLogado)
                        || (oportunidade.getUsuarioId() != null && oportunidade.getUsuarioId().equals(usuarioId))
                        || (oportunidade.getUsuarioResponsavel() != null
                                && oportunidade.getUsuarioResponsavel().getId() != null
                                && oportunidade.getUsuarioResponsavel().getId().equals(usuarioId)))
                .toList();

        return ResponseEntity.ok(oportunidades.stream().map(this::toDTO).toList());
    }

    @GetMapping("/relatorio/vendas")
    public ResponseEntity<RelatorioVendasDTO> getRelatorioVendas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();

        List<OportunidadeEntity> oportunidades = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(o -> o.getStatus() == StatusOportunidade.GANHA || o.getStatus() == StatusOportunidade.PERDIDA)
                .filter(o -> {
                    LocalDate dataReferencia = o.getDataFechamentoReal() != null ? o.getDataFechamentoReal()
                            : (o.getCreatedAt() != null ? o.getCreatedAt().toLocalDate() : null);
                    return dataReferencia != null
                            && !dataReferencia.isBefore(dataInicio)
                            && !dataReferencia.isAfter(dataFim);
                })
                .toList();

        BigDecimal totalVendido = BigDecimal.ZERO;
        BigDecimal totalPerdido = BigDecimal.ZERO;
        long quantidadeVendas = 0L;
        long quantidadePerdidas = 0L;
        Map<LocalDate, VendaPorDiaDTO> vendasPorDiaMap = new LinkedHashMap<>();

        for (OportunidadeEntity oportunidade : oportunidades) {
            LocalDate dataReferencia = oportunidade.getDataFechamentoReal() != null
                    ? oportunidade.getDataFechamentoReal()
                    : (oportunidade.getCreatedAt() != null ? oportunidade.getCreatedAt().toLocalDate() : null);
            if (dataReferencia == null) {
                continue;
            }

            if (oportunidade.getStatus() == StatusOportunidade.GANHA) {
                quantidadeVendas++;
                totalVendido = totalVendido
                        .add(oportunidade.getValor() != null ? oportunidade.getValor() : BigDecimal.ZERO);

                VendaPorDiaDTO vendaDia = vendasPorDiaMap.computeIfAbsent(dataReferencia,
                        dia -> new VendaPorDiaDTO(dia, BigDecimal.ZERO, 0L));
                vendaDia.setValor(vendaDia.getValor()
                        .add(oportunidade.getValor() != null ? oportunidade.getValor() : BigDecimal.ZERO));
                vendaDia.setQuantidade(vendaDia.getQuantidade() + 1);
            }

            if (oportunidade.getStatus() == StatusOportunidade.PERDIDA) {
                quantidadePerdidas++;
                totalPerdido = totalPerdido
                        .add(oportunidade.getValor() != null ? oportunidade.getValor() : BigDecimal.ZERO);
            }
        }

        BigDecimal ticketMedio = quantidadeVendas == 0 ? BigDecimal.ZERO
                : totalVendido.divide(BigDecimal.valueOf(quantidadeVendas), 2, java.math.RoundingMode.HALF_UP);

        double totalFechadas = quantidadeVendas + quantidadePerdidas;
        double taxaConversaoPeriodo = totalFechadas == 0 ? 0.0
                : (quantidadeVendas * 100.0) / totalFechadas;

        RelatorioVendasDTO relatorio = new RelatorioVendasDTO();
        relatorio.setTotalVendido(totalVendido);
        relatorio.setQuantidadeVendas(quantidadeVendas);
        relatorio.setTicketMedio(ticketMedio);
        relatorio.setQuantidadePerdidas(quantidadePerdidas);
        relatorio.setTotalPerdido(totalPerdido);
        relatorio.setTaxaConversaoPeriodo(taxaConversaoPeriodo);
        relatorio.setVendasPorDia(new ArrayList<>(vendasPorDiaMap.values()));

        return ResponseEntity.ok(relatorio);
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<OportunidadeDTO> fecharOportunidade(@PathVariable Long id,
            @RequestBody FecharOportunidadeRequest request) {
        OportunidadeEntity oportunidade = oportunidadeService.fecharOportunidade(id, request.getStatus(),
                request.getMotivo(), request.getValorFinal());

        return ResponseEntity.ok(toDTO(oportunidade));
    }

    @PatchMapping("/{id}/etapa")
    public ResponseEntity<OportunidadeDTO> moverEtapa(@PathVariable Long id,
            @RequestBody MoverEtapaRequest request) {
        OportunidadeEntity oportunidade = oportunidadeService.moverEtapa(id, request.getEtapa(), request.getMotivo());
        return ResponseEntity.ok(toDTO(oportunidade));
    }

    private OportunidadeDTO toDTO(OportunidadeEntity entity) {
        OportunidadeDTO dto = new OportunidadeDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescricao(entity.getDescricao());
        dto.setEtapa(entity.getEtapa());
        dto.setStatus(entity.getStatus());
        dto.setValor(entity.getValor());
        dto.setProbabilidade(entity.getProbabilidade());
        dto.setDataFechamentoEstimada(entity.getDataFechamentoEstimada());
        dto.setDataFechamentoReal(entity.getDataFechamentoReal());
        dto.setMotivoPerda(entity.getMotivoPerda());
        dto.setAlertaAtivo(entity.getAlertaAtivo());
        return dto;
    }

    private boolean isAdminOrOwner(com.api.HbSolution.entity.UsuarioEntity usuario) {
        if (usuario == null || usuario.getRole() == null) {
            return false;
        }

        String role = usuario.getRole().trim().toUpperCase();
        return role.equals("ADMIN") || role.equals("DONO") || role.equals("ROLE_ADMIN") || role.equals("ROLE_DONO");
    }
}

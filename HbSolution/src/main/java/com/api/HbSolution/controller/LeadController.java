package com.api.HbSolution.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.HbSolution.DTO.ImportacaoResultadoDTO;
import com.api.HbSolution.DTO.LeadDTO;
import com.api.HbSolution.DTO.OportunidadeDTO;
import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusLead;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.LeadService;

@RestController
@RequestMapping("/api/leads")
public class LeadController extends BaseController<LeadEntity, LeadService> {

    private final LeadService leadService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<LeadEntity>> findByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(leadService.findByEmpresaId(empresaId));
    }

    @GetMapping("/ativos/empresa/{empresaId}")
    public ResponseEntity<List<LeadEntity>> findAtivosByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(leadService.findAtivosByEmpresaId(empresaId));
    }

    @GetMapping("/busca")
    public ResponseEntity<Page<LeadDTO>> buscarLeads(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) StatusLead status,
            @RequestParam(required = false) OrigemLead origem,
            @RequestParam(required = false) Integer scoreMinimo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();
        Pageable pageable = PageRequest.of(page, size);
        Page<LeadEntity> leads = leadService.buscarLeadsFiltrados(empresaId, nome, telefone, status, origem,
                scoreMinimo, pageable);

        return ResponseEntity.ok(leads.map(this::toDTO));
    }

    @GetMapping("/meus")
    public ResponseEntity<Page<LeadDTO>> getMeusLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var usuarioLogado = SecurityUtils.getUsuarioLogado();
        Long empresaId = usuarioLogado.getEmpresaId();
        Long usuarioId = usuarioLogado.getId();

        List<LeadEntity> leads = leadService.findByEmpresaId(empresaId).stream()
                .filter(lead -> isAdminOrOwner(usuarioLogado)
                        || (lead.getUsuarioId() != null && lead.getUsuarioId().equals(usuarioId)))
                .toList();

        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), leads.size());
        int end = Math.min(start + pageable.getPageSize(), leads.size());
        List<LeadEntity> pageContent = leads.subList(start, end);

        return ResponseEntity
                .ok(new org.springframework.data.domain.PageImpl<>(pageContent.stream().map(this::toDTO).toList(),
                        pageable, leads.size()));
    }

    @PostMapping("/importar")
    public ResponseEntity<ImportacaoResultadoDTO> importarLeads(@RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            ImportacaoResultadoDTO erro = new ImportacaoResultadoDTO();
            erro.setTotalErros(1);
            erro.setErros(List.of("Arquivo vazio ou não informado."));
            return ResponseEntity.badRequest().body(erro);
        }

        return ResponseEntity.ok(leadService.importarLeads(arquivo));
    }

    @PostMapping("/{id}/atualizar-score")
    public ResponseEntity<LeadDTO> atualizarScore(@PathVariable Long id) {
        LeadEntity lead = leadService.buscarPorIdValidandoEmpresa(id);
        lead.setScore(leadService.calcularScoreAutomatico(lead));
        LeadEntity atualizado = leadService.save(lead);

        LeadDTO dto = new LeadDTO();
        dto.setId(atualizado.getId());
        dto.setNome(atualizado.getNome());
        dto.setEmail(atualizado.getEmail());
        dto.setTelefone(atualizado.getTelefone());
        dto.setOrigem(atualizado.getOrigem());
        dto.setStatus(atualizado.getStatus());
        dto.setScore(atualizado.getScore());
        dto.setObservacao(atualizado.getObservacao());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/converter")
    public ResponseEntity<OportunidadeDTO> converterLead(@PathVariable Long id) {
        OportunidadeEntity oportunidade = leadService.converterLeadEmOportunidade(id);

        OportunidadeDTO dto = new OportunidadeDTO();
        dto.setId(oportunidade.getId());
        dto.setTitulo(oportunidade.getTitulo());
        dto.setDescricao(oportunidade.getDescricao());
        dto.setEtapa(oportunidade.getEtapa());
        dto.setStatus(oportunidade.getStatus());
        dto.setValor(oportunidade.getValor());
        dto.setProbabilidade(oportunidade.getProbabilidade());
        dto.setDataFechamentoEstimada(oportunidade.getDataFechamentoEstimada());
        dto.setDataFechamentoReal(oportunidade.getDataFechamentoReal());
        dto.setMotivoPerda(oportunidade.getMotivoPerda());
        dto.setAlertaAtivo(oportunidade.getAlertaAtivo());

        return ResponseEntity.ok(dto);
    }

    private LeadDTO toDTO(LeadEntity entity) {
        LeadDTO dto = new LeadDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setOrigem(entity.getOrigem());
        dto.setStatus(entity.getStatus());
        dto.setScore(entity.getScore());
        dto.setObservacao(entity.getObservacao());
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

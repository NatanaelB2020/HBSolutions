package com.api.HbSolution.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.AtividadeDTO;
import com.api.HbSolution.DTO.RealizarAtividadeRequest;
import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.enums.StatusAtividade;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.AtividadeService;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController extends BaseController<AtividadeEntity, AtividadeService> {

    private final AtividadeService atividadeService;

    @Autowired
    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<AtividadeEntity>> findByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(atividadeService.findByEmpresaId(empresaId));
    }

    @GetMapping("/ativos/empresa/{empresaId}")
    public ResponseEntity<List<AtividadeEntity>> findAtivosByEmpresaId(@PathVariable Long empresaId) {
        return ResponseEntity.ok(atividadeService.findAtivosByEmpresaId(empresaId));
    }

    @GetMapping("/oportunidade/{oportunidadeId}")
    public ResponseEntity<List<AtividadeEntity>> findByOportunidadeId(@PathVariable Long oportunidadeId) {
        return ResponseEntity.ok(atividadeService.findByOportunidadeId(oportunidadeId));
    }

    @GetMapping("/pendentes/usuario/{usuarioId}")
    public ResponseEntity<List<AtividadeEntity>> findPendentesByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(atividadeService.findPendentesByUsuarioId(usuarioId));
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AtividadeDTO>> getAtividadesHoje() {
        Long usuarioId = SecurityUtils.getUsuarioLogado().getId();
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(hoje, LocalTime.MAX);

        List<AtividadeEntity> atividades = atividadeService.findPendentesHojeByUsuarioId(usuarioId, inicioDoDia,
                fimDoDia);

        List<AtividadeDTO> dtoList = atividades.stream()
                .sorted(Comparator.comparing(AtividadeEntity::getDataAgendamento,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/atrasadas")
    public ResponseEntity<List<AtividadeDTO>> getAtividadesAtrasadas() {
        Long usuarioId = SecurityUtils.getUsuarioLogado().getId();
        LocalDateTime agora = LocalDateTime.now();

        List<AtividadeEntity> atividades = atividadeService.findPendentesAtrasadasByUsuarioId(usuarioId, agora);

        List<AtividadeDTO> dtoList = atividades.stream()
                .sorted(Comparator.comparing(AtividadeEntity::getDataAgendamento,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<AtividadeDTO>> getMinhasAtividades() {
        var usuarioLogado = SecurityUtils.getUsuarioLogado();
        Long empresaId = usuarioLogado.getEmpresaId();
        Long usuarioId = usuarioLogado.getId();

        List<AtividadeEntity> atividades = atividadeService.findByEmpresaId(empresaId).stream()
                .filter(atividade -> isAdminOrOwner(usuarioLogado)
                        || (atividade.getUsuarioId() != null && atividade.getUsuarioId().equals(usuarioId))
                        || (atividade.getUsuarioResponsavel() != null
                                && atividade.getUsuarioResponsavel().getId() != null
                                && atividade.getUsuarioResponsavel().getId().equals(usuarioId)))
                .toList();

        return ResponseEntity.ok(atividades.stream().map(this::toDTO).toList());
    }

    @PostMapping("/{id}/realizar")
    public ResponseEntity<AtividadeDTO> realizarAtividade(@PathVariable Long id,
            @RequestBody RealizarAtividadeRequest request) {
        AtividadeEntity atividade = atividadeService.realizarAtividade(id, request);
        return ResponseEntity.ok(toDTO(atividade));
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

    private boolean isAdminOrOwner(com.api.HbSolution.entity.UsuarioEntity usuario) {
        if (usuario == null || usuario.getRole() == null) {
            return false;
        }

        String role = usuario.getRole().trim().toUpperCase();
        return role.equals("ADMIN") || role.equals("DONO") || role.equals("ROLE_ADMIN") || role.equals("ROLE_DONO");
    }
}

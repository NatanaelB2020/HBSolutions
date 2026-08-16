package com.api.HbSolution.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.AtividadeResumoDTO;
import com.api.HbSolution.DTO.ClienteDTO;
import com.api.HbSolution.DTO.ClienteHistoricoDTO;
import com.api.HbSolution.DTO.LeadResumoDTO;
import com.api.HbSolution.DTO.OportunidadeResumoDTO;
import com.api.HbSolution.DTO.PedidoResumoDTO;
import com.api.HbSolution.DTO.TimelineEventDTO;
import com.api.HbSolution.entity.AtividadeEntity;
import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.PedidoEntity;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.AtividadeService;
import com.api.HbSolution.service.ClienteService;
import com.api.HbSolution.service.LeadService;
import com.api.HbSolution.service.OportunidadeService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteHistoricoController {

    private final ClienteService clienteService;
    private final LeadService leadService;
    private final OportunidadeService oportunidadeService;
    private final AtividadeService atividadeService;

    @Autowired
    public ClienteHistoricoController(ClienteService clienteService, LeadService leadService,
            OportunidadeService oportunidadeService, AtividadeService atividadeService) {
        this.clienteService = clienteService;
        this.leadService = leadService;
        this.oportunidadeService = oportunidadeService;
        this.atividadeService = atividadeService;
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<ClienteHistoricoDTO> getHistoricoCliente(@PathVariable Long id) {
        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();

        ClienteEntity cliente = clienteService.findById(id)
                .filter(c -> c.getEmpresaId() != null && c.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para a empresa do usuário"));

        ClienteDTO clienteDTO = toClienteDTO(cliente);

        List<LeadEntity> leads = leadService.findByEmpresaId(empresaId).stream()
                .filter(lead -> lead.getEmail() != null && cliente.getEmail() != null
                        && lead.getEmail().equalsIgnoreCase(cliente.getEmail())
                        || lead.getTelefone() != null && cliente.getTelefone() != null
                                && lead.getTelefone().equals(cliente.getTelefone()))
                .collect(Collectors.toList());

        List<OportunidadeEntity> oportunidades = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(o -> o.getCliente() != null && o.getCliente().getId().equals(cliente.getId()))
                .collect(Collectors.toList());

        List<AtividadeEntity> atividades = new ArrayList<>();
        for (OportunidadeEntity oportunidade : oportunidades) {
            atividades.addAll(atividadeService.findByOportunidadeId(oportunidade.getId()));
        }

        List<PedidoEntity> pedidos = oportunidadeService.findByEmpresaId(empresaId).stream()
                .filter(o -> o.getCliente() != null && o.getCliente().getId().equals(cliente.getId()))
                .flatMap(o -> o.getPedidos() == null ? java.util.stream.Stream.empty() : o.getPedidos().stream())
                .collect(Collectors.toList());

        ClienteHistoricoDTO historico = new ClienteHistoricoDTO();
        historico.setCliente(clienteDTO);
        historico.setLeads(leads.stream().map(this::toLeadResumoDTO).collect(Collectors.toList()));
        historico.setOportunidades(
                oportunidades.stream().map(this::toOportunidadeResumoDTO).collect(Collectors.toList()));
        historico.setAtividades(atividades.stream().map(this::toAtividadeResumoDTO).collect(Collectors.toList()));
        historico.setPedidos(pedidos.stream().map(this::toPedidoResumoDTO).collect(Collectors.toList()));
        historico.setTimeline(buildTimeline(leads, oportunidades, atividades, pedidos));

        return ResponseEntity.ok(historico);
    }

    private List<TimelineEventDTO> buildTimeline(List<LeadEntity> leads, List<OportunidadeEntity> oportunidades,
            List<AtividadeEntity> atividades, List<PedidoEntity> pedidos) {
        List<TimelineEventDTO> timeline = new ArrayList<>();

        for (LeadEntity lead : leads) {
            TimelineEventDTO event = new TimelineEventDTO();
            event.setTipo("LEAD");
            event.setDescricao("Lead " + lead.getNome() + " foi convertido para o cliente");
            event.setData(lead.getDataConversao() != null ? lead.getDataConversao() : lead.getCreatedAt());
            event.setStatus(lead.getStatus() != null ? lead.getStatus().name() : null);
            timeline.add(event);
        }

        for (OportunidadeEntity oportunidade : oportunidades) {
            TimelineEventDTO event = new TimelineEventDTO();
            event.setTipo("OPORTUNIDADE");
            event.setDescricao("Oportunidade " + oportunidade.getTitulo() + " movida para " + oportunidade.getEtapa());
            event.setData(
                    oportunidade.getUpdatedAt() != null ? oportunidade.getUpdatedAt() : oportunidade.getCreatedAt());
            event.setStatus(oportunidade.getStatus() != null ? oportunidade.getStatus().name() : null);
            event.setValor(oportunidade.getValor());
            timeline.add(event);
        }

        for (AtividadeEntity atividade : atividades) {
            TimelineEventDTO event = new TimelineEventDTO();
            event.setTipo("ATIVIDADE");
            event.setDescricao(atividade.getTitulo());
            event.setData(atividade.getDataAgendamento() != null ? atividade.getDataAgendamento()
                    : atividade.getDataAtividade());
            event.setStatus(atividade.getStatus() != null ? atividade.getStatus().name() : null);
            timeline.add(event);
        }

        for (PedidoEntity pedido : pedidos) {
            TimelineEventDTO event = new TimelineEventDTO();
            event.setTipo("PEDIDO");
            event.setDescricao("Pedido " + pedido.getId());
            event.setData(pedido.getDataPedido());
            event.setStatus(pedido.getStatus());
            event.setValor(pedido.getValorTotal());
            timeline.add(event);
        }

        timeline.sort(Comparator.comparing(TimelineEventDTO::getData, Comparator.nullsLast(LocalDateTime::compareTo))
                .reversed());
        return timeline;
    }

    private ClienteDTO toClienteDTO(ClienteEntity entity) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCpf(entity.getCpf());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        return dto;
    }

    private LeadResumoDTO toLeadResumoDTO(LeadEntity entity) {
        LeadResumoDTO dto = new LeadResumoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setStatus(entity.getStatus());
        dto.setScore(entity.getScore());
        dto.setDataConversao(entity.getDataConversao());
        return dto;
    }

    private OportunidadeResumoDTO toOportunidadeResumoDTO(OportunidadeEntity entity) {
        OportunidadeResumoDTO dto = new OportunidadeResumoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setEtapa(entity.getEtapa());
        dto.setStatus(entity.getStatus());
        dto.setValor(entity.getValor());
        return dto;
    }

    private AtividadeResumoDTO toAtividadeResumoDTO(AtividadeEntity entity) {
        AtividadeResumoDTO dto = new AtividadeResumoDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setTipo(entity.getTipo());
        dto.setStatus(entity.getStatus());
        dto.setDataAgendamento(entity.getDataAgendamento());
        dto.setResultado(entity.getResultado());
        return dto;
    }

    private PedidoResumoDTO toPedidoResumoDTO(PedidoEntity entity) {
        PedidoResumoDTO dto = new PedidoResumoDTO();
        dto.setId(entity.getId());
        dto.setDataPedido(entity.getDataPedido());
        dto.setValorTotal(entity.getValorTotal());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}

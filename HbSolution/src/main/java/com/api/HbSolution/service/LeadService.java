package com.api.HbSolution.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.HbSolution.DTO.ImportacaoResultadoDTO;
import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusLead;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.exception.ResourceNotFoundException;
import com.api.HbSolution.repository.LeadRepository;
import com.api.HbSolution.repository.OportunidadeRepository;
import com.api.HbSolution.repository.UsuarioRepository;
import com.api.HbSolution.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class LeadService extends BaseService<LeadEntity> {

    private final LeadRepository leadRepository;
    private final OportunidadeRepository oportunidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final OportunidadeService oportunidadeService;

    @Autowired
    public LeadService(LeadRepository leadRepository, OportunidadeRepository oportunidadeRepository,
            UsuarioRepository usuarioRepository, OportunidadeService oportunidadeService) {
        super.setRepository(leadRepository);
        this.leadRepository = leadRepository;
        this.oportunidadeRepository = oportunidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.oportunidadeService = oportunidadeService;
    }

    @Override
    public LeadEntity save(LeadEntity lead) {
        if (lead == null) {
            throw new IllegalArgumentException("Lead não informado");
        }

        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        Long empresaId = lead.getEmpresaId() != null ? lead.getEmpresaId() : usuarioLogado.getEmpresaId();
        lead.setEmpresaId(empresaId);

        String emailNormalizado = normalizarCampo(lead.getEmail());
        String telefoneNormalizado = normalizarCampo(lead.getTelefone());

        LeadEntity leadExistente = null;
        if (emailNormalizado != null) {
            leadExistente = leadRepository
                    .findByEmpresaIdAndEmailAndAtivo(empresaId, emailNormalizado, StatusAtivo.ATIVO)
                    .orElse(null);
        }

        if (leadExistente == null && telefoneNormalizado != null) {
            leadExistente = leadRepository.findByEmpresaIdAndTelefoneAndAtivo(empresaId, telefoneNormalizado,
                    StatusAtivo.ATIVO).orElse(null);
        }

        if (leadExistente != null) {
            if (lead.getOrigem() != null && !lead.getOrigem().equals(leadExistente.getOrigem())) {
                leadExistente.setOrigem(lead.getOrigem());
            }

            if (lead.getScore() != null
                    && (leadExistente.getScore() == null || lead.getScore() > leadExistente.getScore())) {
                leadExistente.setScore(lead.getScore());
            }

            if (lead.getNome() != null && !lead.getNome().isBlank()) {
                leadExistente.setNome(lead.getNome());
            }
            if (emailNormalizado != null) {
                leadExistente.setEmail(emailNormalizado);
            }
            if (telefoneNormalizado != null) {
                leadExistente.setTelefone(telefoneNormalizado);
            }

            if (lead.getStatus() != null) {
                leadExistente.setStatus(lead.getStatus());
            }

            if (lead.getObservacao() != null && !lead.getObservacao().isBlank()) {
                String observacaoAtual = leadExistente.getObservacao() == null ? "" : leadExistente.getObservacao();
                String observacaoNova = observacaoAtual.isBlank() ? lead.getObservacao()
                        : observacaoAtual + " | Atualizado em: " + LocalDateTime.now() + " | " + lead.getObservacao();
                leadExistente.setObservacao(observacaoNova);
            }

            if (leadExistente.getUsuarioId() == null) {
                atribuirLeadAoUsuarioMaisDisponivel(leadExistente);
            }

            leadExistente.setScore(calcularScoreAutomatico(leadExistente));
            return leadRepository.save(leadExistente);
        }

        if (lead.getUsuarioId() == null) {
            atribuirLeadAoUsuarioMaisDisponivel(lead);
        }

        lead.setScore(calcularScoreAutomatico(lead));
        return leadRepository.save(lead);
    }

    private void atribuirLeadAoUsuarioMaisDisponivel(LeadEntity lead) {
        Long empresaId = lead.getEmpresaId() != null ? lead.getEmpresaId()
                : SecurityUtils.getUsuarioLogado().getEmpresaId();
        List<UsuarioEntity> usuarios = usuarioRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);

        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }

        if (usuarios.size() == 1) {
            lead.setUsuarioId(usuarios.get(0).getId());
            return;
        }

        UsuarioEntity melhorUsuario = usuarios.stream()
                .sorted(Comparator.comparingLong(usuario -> oportunidadeRepository.countByUsuarioResponsavelIdAndStatus(
                        usuario.getId(), StatusOportunidade.ABERTA)))
                .findFirst()
                .orElse(usuarios.get(0));

        lead.setUsuarioId(melhorUsuario.getId());
    }

    private String normalizarCampo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public Integer calcularScoreAutomatico(LeadEntity lead) {
        if (lead == null) {
            return 0;
        }

        int score = 0;

        if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
            score += 10;
        }

        if (lead.getTelefone() != null && !lead.getTelefone().isBlank()) {
            score += 10;
        }

        if (lead.getOrigem() != null) {
            switch (lead.getOrigem()) {
                case INDICACAO -> score += 30;
                case WHATSAPP -> score += 20;
                case SITE -> score += 15;
                case FEIRA -> score += 10;
                case REDE_SOCIAL -> score += 5;
                case OUTROS -> score += 0;
                default -> score += 0;
            }
        }

        if (lead.getStatus() != null) {
            switch (lead.getStatus()) {
                case EM_CONTATO -> score += 15;
                case QUALIFICADO -> score += 50;
                case DESQUALIFICADO -> score -= 50;
                default -> score += 0;
            }
        }

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }

        return score;
    }

    public List<LeadEntity> findByEmpresaId(Long empresaId) {
        return leadRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<LeadEntity> findAtivosByEmpresaId(Long empresaId) {
        return leadRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO);
    }

    public List<LeadEntity> findNovosNoMes(Long empresaId) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = LocalDateTime.of(hoje.withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime fim = LocalDateTime.of(hoje.withDayOfMonth(hoje.lengthOfMonth()), LocalTime.MAX);
        return leadRepository.findAllByEmpresaIdAndCreatedAtBetween(empresaId, inicio, fim);
    }

    public ImportacaoResultadoDTO importarLeads(MultipartFile arquivo) {
        ImportacaoResultadoDTO resultado = new ImportacaoResultadoDTO();
        resultado.setErros(new ArrayList<>());

        if (arquivo == null || arquivo.isEmpty()) {
            resultado.setTotalErros(1);
            resultado.getErros().add("Arquivo vazio ou não informado.");
            return resultado;
        }

        Long empresaId = SecurityUtils.getUsuarioLogado().getEmpresaId();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setDelimiter(';')
                        .setIgnoreSurroundingSpaces(true)
                        .setTrim(true)
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)) {

            List<String> cabecalhoEsperado = List.of("nome", "email", "telefone", "origem", "observacao");
            List<String> cabecalhoLido = parser.getHeaderNames();
            if (cabecalhoLido == null || cabecalhoLido.isEmpty()) {
                resultado.setTotalErros(1);
                resultado.getErros().add("Arquivo CSV sem cabeçalho.");
                return resultado;
            }

            List<String> cabecalhoNormalizado = cabecalhoLido.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            if (!cabecalhoNormalizado.equals(cabecalhoEsperado)) {
                resultado.setTotalErros(1);
                resultado.getErros().add("Cabeçalho inválido. Esperado: nome;email;telefone;origem;observacao");
                return resultado;
            }

            for (CSVRecord record : parser) {
                resultado.setTotalLidos(resultado.getTotalLidos() + 1);

                if (record.size() < 5) {
                    registrarErro(resultado,
                            "Linha " + resultado.getTotalLidos() + ": quantidade de colunas inválida.");
                    continue;
                }

                String nome = limparTexto(record.get(0));
                String email = limparTexto(record.get(1));
                String telefone = limparTexto(record.get(2));
                String origemTexto = limparTexto(record.get(3));
                String observacao = limparTexto(record.get(4));

                if (nome == null || nome.isBlank()) {
                    registrarErro(resultado, "Linha " + resultado.getTotalLidos() + ": nome obrigatório.");
                    continue;
                }

                if ((email == null || email.isBlank()) && (telefone == null || telefone.isBlank())) {
                    registrarErro(resultado, "Linha " + resultado.getTotalLidos() + ": email ou telefone obrigatório.");
                    continue;
                }

                email = normalizarCampo(email);
                telefone = normalizarCampo(telefone);
                OrigemLead origem = resolverOrigem(origemTexto);

                LeadEntity leadExistente = null;
                if (email != null) {
                    leadExistente = leadRepository.findByEmpresaIdAndEmailAndAtivo(empresaId, email, StatusAtivo.ATIVO)
                            .orElse(null);
                }
                if (leadExistente == null && telefone != null) {
                    leadExistente = leadRepository
                            .findByEmpresaIdAndTelefoneAndAtivo(empresaId, telefone, StatusAtivo.ATIVO)
                            .orElse(null);
                }

                LeadEntity lead = leadExistente != null ? leadExistente : new LeadEntity();
                lead.setEmpresaId(empresaId);
                lead.setNome(nome);
                lead.setEmail(email);
                lead.setTelefone(telefone);
                lead.setOrigem(origem != null ? origem : OrigemLead.OUTROS);
                lead.setStatus(StatusLead.NOVO);
                lead.setObservacao(observacao);

                if (leadExistente != null) {
                    resultado.setTotalAtualizados(resultado.getTotalAtualizados() + 1);
                } else {
                    resultado.setTotalImportados(resultado.getTotalImportados() + 1);
                }

                lead.setScore(calcularScoreAutomatico(lead));
                if (lead.getUsuarioId() == null) {
                    atribuirLeadAoUsuarioMaisDisponivel(lead);
                }

                save(lead);
            }

        } catch (IOException e) {
            resultado.setTotalErros(resultado.getTotalErros() + 1);
            if (resultado.getErros().size() < 10) {
                resultado.getErros().add("Erro ao ler o arquivo CSV: " + e.getMessage());
            }
        }

        return resultado;
    }

    private void registrarErro(ImportacaoResultadoDTO resultado, String mensagem) {
        resultado.setTotalErros(resultado.getTotalErros() + 1);
        if (resultado.getErros().size() < 10) {
            resultado.getErros().add(mensagem);
        }
    }

    private String limparTexto(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim();
    }

    private OrigemLead resolverOrigem(String origemTexto) {
        if (origemTexto == null || origemTexto.isBlank()) {
            return OrigemLead.OUTROS;
        }

        for (OrigemLead origem : OrigemLead.values()) {
            if (origem.name().equalsIgnoreCase(origemTexto)
                    || origem.getDescricao().equalsIgnoreCase(origemTexto)
                    || origem.name().replace("_", " ").equalsIgnoreCase(origemTexto.replace("_", " "))) {
                return origem;
            }
        }

        return OrigemLead.OUTROS;
    }

    public LeadEntity buscarPorIdValidandoEmpresa(Long id) {
        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        Long empresaId = usuarioLogado.getEmpresaId();
        return leadRepository.findByIdAndEmpresaIdAndAtivo(id, empresaId, StatusAtivo.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado para a empresa do usuário"));
    }

    public Page<LeadEntity> buscarLeadsFiltrados(Long empresaId, String nome, String telefone,
            com.api.HbSolution.enums.StatusLead status, com.api.HbSolution.enums.OrigemLead origem,
            Integer scoreMinimo, Pageable pageable) {
        return leadRepository.buscarLeadsFiltrados(empresaId, StatusAtivo.ATIVO, nome, telefone, status, origem,
                scoreMinimo, pageable);
    }

    @Transactional
    public OportunidadeEntity converterLeadEmOportunidade(Long leadId) {
        LeadEntity lead = buscarPorIdValidandoEmpresa(leadId);

        if (lead.getStatus() != StatusLead.QUALIFICADO) {
            throw new IllegalStateException("Lead precisa estar qualificado");
        }

        UsuarioEntity usuarioLogado = SecurityUtils.getUsuarioLogado();

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setTitulo("Oportunidade - " + lead.getNome());
        oportunidade.setDescricao(lead.getObservacao());
        oportunidade.setEtapa(EtapaOportunidade.PROSPECCAO);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setValor(null);
        oportunidade.setProbabilidade(25);
        oportunidade.setLead(lead);
        oportunidade.setCliente(null);
        oportunidade.setUsuarioResponsavel(usuarioLogado);
        oportunidade.setEmpresa(usuarioLogado.getEmpresa());
        oportunidade.setAlertaAtivo(false);

        OportunidadeEntity oportunidadeSalva = oportunidadeService.save(oportunidade);

        lead.setStatus(StatusLead.CONVERTIDO);
        lead.setDataConversao(LocalDateTime.now());
        save(lead);

        return oportunidadeSalva;
    }
}

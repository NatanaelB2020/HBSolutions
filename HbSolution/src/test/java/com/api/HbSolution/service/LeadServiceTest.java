package com.api.HbSolution.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusLead;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.repository.LeadRepository;
import com.api.HbSolution.repository.OportunidadeRepository;
import com.api.HbSolution.repository.UsuarioRepository;
import com.api.HbSolution.security.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private OportunidadeRepository oportunidadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private OportunidadeService oportunidadeService;

    @InjectMocks
    private LeadService leadService;

    @Test
    void calcularScoreAutomatico_emailTelefoneOrigemIndicacaoEStatusQualificado_retorna100() {
        LeadEntity lead = new LeadEntity();
        lead.setEmail("cliente@teste.com");
        lead.setTelefone("11999999999");
        lead.setOrigem(OrigemLead.INDICACAO);
        lead.setStatus(StatusLead.QUALIFICADO);

        assertEquals(100, leadService.calcularScoreAutomatico(lead));
    }

    @Test
    void calcularScoreAutomatico_semDadosOrigemOutrosEStatusNovo_retorna0() {
        LeadEntity lead = new LeadEntity();
        lead.setEmail(null);
        lead.setTelefone(null);
        lead.setOrigem(OrigemLead.OUTROS);
        lead.setStatus(StatusLead.NOVO);

        assertEquals(0, leadService.calcularScoreAutomatico(lead));
    }

    @Test
    void calcularScoreAutomatico_desqualificado_nuncaNegativo() {
        LeadEntity lead = new LeadEntity();
        lead.setEmail("teste@teste.com");
        lead.setTelefone("11999999999");
        lead.setOrigem(OrigemLead.OUTROS);
        lead.setStatus(StatusLead.DESQUALIFICADO);

        assertEquals(0, leadService.calcularScoreAutomatico(lead));
    }

    @Test
    void save_leadDuplicado_comEmailExistente_retornaLeadExistenteAtualizado() {
        Long empresaId = 10L;
        LeadEntity existente = new LeadEntity();
        existente.setId(1L);
        existente.setEmpresaId(empresaId);
        existente.setNome("Maria");
        existente.setEmail("maria@teste.com");
        existente.setTelefone("11911112222");
        existente.setOrigem(OrigemLead.OUTROS);
        existente.setStatus(StatusLead.NOVO);
        existente.setScore(10);
        existente.setAtivo(StatusAtivo.ATIVO);

        LeadEntity novo = new LeadEntity();
        novo.setNome("Maria Nova");
        novo.setEmail("maria@teste.com");
        novo.setTelefone("11911112222");
        novo.setOrigem(OrigemLead.INDICACAO);
        novo.setStatus(StatusLead.QUALIFICADO);
        novo.setEmpresaId(empresaId);

        when(leadRepository.findByEmpresaIdAndEmailAndAtivo(empresaId, "maria@teste.com", StatusAtivo.ATIVO))
                .thenReturn(Optional.of(existente));
        when(leadRepository.save(any(LeadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(5L);
            usuario.setEmpresaId(empresaId);
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            LeadEntity salvo = leadService.save(novo);

            assertSame(existente, salvo);
            assertEquals("Maria Nova", salvo.getNome());
            assertEquals(OrigemLead.INDICACAO, salvo.getOrigem());
            assertEquals(StatusLead.QUALIFICADO, salvo.getStatus());
            assertNotNull(salvo.getScore());
            verify(leadRepository).save(existente);
        }
    }

    @Test
    void save_leadSemDuplicidade_criaNovoLead() {
        Long empresaId = 20L;
        LeadEntity lead = new LeadEntity();
        lead.setNome("Joao");
        lead.setEmail("joao@teste.com");
        lead.setTelefone("11988887777");
        lead.setOrigem(OrigemLead.SITE);
        lead.setStatus(StatusLead.NOVO);
        lead.setEmpresaId(empresaId);

        when(leadRepository.findByEmpresaIdAndEmailAndAtivo(empresaId, "joao@teste.com", StatusAtivo.ATIVO))
                .thenReturn(Optional.empty());
        when(leadRepository.save(any(LeadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(7L);
            usuario.setEmpresaId(empresaId);
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            LeadEntity salvo = leadService.save(lead);

            assertSame(lead, salvo);
            assertNotNull(salvo.getScore());
            verify(leadRepository).save(lead);
        }
    }

    @Test
    void save_leadSemUsuarioId_atribuiAoVendedorComMenosOportunidades() {
        Long empresaId = 30L;
        LeadEntity lead = new LeadEntity();
        lead.setNome("Ana");
        lead.setEmail("ana@teste.com");
        lead.setTelefone("11933334444");
        lead.setOrigem(OrigemLead.WHATSAPP);
        lead.setStatus(StatusLead.NOVO);
        lead.setEmpresaId(empresaId);
        lead.setUsuarioId(null);

        UsuarioEntity usuario1 = new UsuarioEntity();
        usuario1.setId(101L);
        usuario1.setEmpresaId(empresaId);

        UsuarioEntity usuario2 = new UsuarioEntity();
        usuario2.setId(102L);
        usuario2.setEmpresaId(empresaId);

        when(leadRepository.findByEmpresaIdAndEmailAndAtivo(empresaId, "ana@teste.com", StatusAtivo.ATIVO))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findAllByEmpresaIdAndAtivo(empresaId, StatusAtivo.ATIVO))
                .thenReturn(List.of(usuario1, usuario2));
        when(oportunidadeRepository.countByUsuarioResponsavelIdAndStatus(101L, StatusOportunidade.ABERTA))
                .thenReturn(4L);
        when(oportunidadeRepository.countByUsuarioResponsavelIdAndStatus(102L, StatusOportunidade.ABERTA))
                .thenReturn(1L);
        when(leadRepository.save(any(LeadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(99L);
            usuario.setEmpresaId(empresaId);
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            LeadEntity salvo = leadService.save(lead);

            assertEquals(102L, salvo.getUsuarioId());
            verify(leadRepository).save(lead);
        }
    }

    @Test
    void converterLeadEmOportunidade_leadQualificado_criaOportunidadeComEtapaProspeccao() {
        Long empresaId = 40L;
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(8L);
        usuario.setEmpresaId(empresaId);

        LeadEntity lead = new LeadEntity();
        lead.setId(1L);
        lead.setNome("Carlos");
        lead.setEmail("carlos@teste.com");
        lead.setEmpresaId(empresaId);
        lead.setStatus(StatusLead.QUALIFICADO);

        when(leadRepository.findByIdAndEmpresaIdAndAtivo(1L, empresaId, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(lead));
        when(leadRepository.save(any(LeadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(oportunidadeService.save(any(OportunidadeEntity.class))).thenAnswer(invocation -> {
            OportunidadeEntity oportunidade = invocation.getArgument(0);
            oportunidade.setId(99L);
            return oportunidade;
        });

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            OportunidadeEntity oportunidade = leadService.converterLeadEmOportunidade(1L);

            assertEquals(EtapaOportunidade.PROSPECCAO, oportunidade.getEtapa());
            assertEquals(StatusOportunidade.ABERTA, oportunidade.getStatus());
            assertEquals(lead, oportunidade.getLead());
            assertEquals(StatusLead.CONVERTIDO, lead.getStatus());
        }
    }

    @Test
    void converterLeadEmOportunidade_leadNovo_lancaIllegalStateException() {
        Long empresaId = 50L;
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(9L);
        usuario.setEmpresaId(empresaId);

        LeadEntity lead = new LeadEntity();
        lead.setId(2L);
        lead.setNome("Pedro");
        lead.setEmpresaId(empresaId);
        lead.setStatus(StatusLead.NOVO);

        when(leadRepository.findByIdAndEmpresaIdAndAtivo(2L, empresaId, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(lead));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            assertThrows(IllegalStateException.class, () -> leadService.converterLeadEmOportunidade(2L));
        }
    }
}

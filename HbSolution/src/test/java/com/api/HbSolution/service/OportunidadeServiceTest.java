package com.api.HbSolution.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.HbSolution.entity.ClienteEntity;
import com.api.HbSolution.entity.LeadEntity;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.PedidoEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.repository.ClienteRepository;
import com.api.HbSolution.repository.OportunidadeRepository;
import com.api.HbSolution.repository.PedidoRepository;
import com.api.HbSolution.security.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class OportunidadeServiceTest {

    @Mock
    private OportunidadeRepository oportunidadeRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private OportunidadeService oportunidadeService;

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioEntity();
        usuario.setId(7L);
        usuario.setEmpresaId(10L);
    }

    @Test
    void fecharOportunidade_ganha_criaClienteEPedidoEAtualizaEtapaEStatus() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(1L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setEtapa(EtapaOportunidade.NEGOCIACAO);
        oportunidade.setTitulo("Venda");

        LeadEntity lead = new LeadEntity();
        lead.setId(11L);
        lead.setNome("Joao");
        lead.setEmail("joao@teste.com");
        lead.setTelefone("11911112222");
        lead.setEmpresaId(10L);
        oportunidade.setLead(lead);

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(1L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));
        when(clienteRepository.findByEmpresaIdAndEmailAndAtivo(10L, "joao@teste.com", StatusAtivo.ATIVO))
                .thenReturn(Optional.empty());
        when(clienteRepository.findByEmpresaIdAndTelefoneAndAtivo(10L, "11911112222", StatusAtivo.ATIVO))
                .thenReturn(Optional.empty());
        when(clienteRepository.save(any(ClienteEntity.class))).thenAnswer(invocation -> {
            ClienteEntity cliente = invocation.getArgument(0);
            cliente.setId(99L);
            return cliente;
        });
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(invocation -> {
            PedidoEntity pedido = invocation.getArgument(0);
            pedido.setId(77L);
            return pedido;
        });
        when(oportunidadeRepository.save(any(OportunidadeEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            OportunidadeEntity salva = oportunidadeService.fecharOportunidade(1L, StatusOportunidade.GANHA, null,
                    new BigDecimal("1500.00"));

            assertEquals(StatusOportunidade.GANHA, salva.getStatus());
            assertEquals(EtapaOportunidade.FECHAMENTO_GANHO, salva.getEtapa());
            assertEquals(new BigDecimal("1500.00"), salva.getValor());
            assertNotNull(salva.getCliente());
            verify(clienteRepository).save(any(ClienteEntity.class));
            verify(pedidoRepository).save(any(PedidoEntity.class));
        }
    }

    @Test
    void fecharOportunidade_perdida_exigeMotivoPreencheMotivoEnaoCriaPedido() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(2L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setEtapa(EtapaOportunidade.NEGOCIACAO);

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(2L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));
        when(oportunidadeRepository.save(any(OportunidadeEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> oportunidadeService.fecharOportunidade(2L, StatusOportunidade.PERDIDA, null, null));

            assertEquals("Motivo é obrigatório para oportunidade perdida", exception.getMessage());

            OportunidadeEntity salva = oportunidadeService.fecharOportunidade(2L, StatusOportunidade.PERDIDA,
                    "Preço alto", null);

            assertEquals(StatusOportunidade.PERDIDA, salva.getStatus());
            assertEquals("Preço alto", salva.getMotivoPerda());
            assertEquals(EtapaOportunidade.FECHAMENTO_PERDIDO, salva.getEtapa());
            verify(pedidoRepository, never()).save(any(PedidoEntity.class));
        }
    }

    @Test
    void fecharOportunidade_jaFechada_lancaIllegalStateException() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(3L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.GANHA);

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(3L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            assertThrows(IllegalStateException.class,
                    () -> oportunidadeService.fecharOportunidade(3L, StatusOportunidade.GANHA, null,
                            new BigDecimal("100.00")));
        }
    }

    @Test
    void moverEtapa_prospeccaoParaQualificacao_sucesso() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(4L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setEtapa(EtapaOportunidade.PROSPECCAO);
        oportunidade.setValor(new BigDecimal("500"));

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(4L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));
        when(oportunidadeRepository.save(any(OportunidadeEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            OportunidadeEntity salva = oportunidadeService.moverEtapa(4L, EtapaOportunidade.QUALIFICACAO, null);

            assertEquals(EtapaOportunidade.QUALIFICACAO, salva.getEtapa());
            assertEquals(StatusOportunidade.ABERTA, salva.getStatus());
        }
    }

    @Test
    void moverEtapa_negociacaoParaFechamentoGanhoSemValor_lancaIllegalStateException() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(5L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setEtapa(EtapaOportunidade.NEGOCIACAO);
        oportunidade.setValor(null);

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(5L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            assertThrows(IllegalStateException.class,
                    () -> oportunidadeService.moverEtapa(5L, EtapaOportunidade.FECHAMENTO_GANHO, null));
        }
    }

    @Test
    void moverEtapa_fechamentoGanhoParaQualificacao_lancaIllegalStateException() {
        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(6L);
        oportunidade.setEmpresaId(10L);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setEtapa(EtapaOportunidade.FECHAMENTO_GANHO);
        oportunidade.setValor(new BigDecimal("1000"));

        when(oportunidadeRepository.findByIdAndEmpresaIdAndAtivo(6L, 10L, StatusAtivo.ATIVO))
                .thenReturn(Optional.of(oportunidade));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);

            assertThrows(IllegalStateException.class,
                    () -> oportunidadeService.moverEtapa(6L, EtapaOportunidade.QUALIFICACAO, null));
        }
    }
}

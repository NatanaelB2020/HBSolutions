package com.api.HbSolution.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.api.HbSolution.DTO.FecharOportunidadeRequest;
import com.api.HbSolution.entity.OportunidadeEntity;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusOportunidade;
import com.api.HbSolution.exception.GlobalExceptionHandler;
import com.api.HbSolution.repository.AtividadeRepository;
import com.api.HbSolution.security.JwtAuthenticationFilter;
import com.api.HbSolution.security.JwtUtil;
import com.api.HbSolution.security.SecurityUtils;
import com.api.HbSolution.service.LeadService;
import com.api.HbSolution.service.OportunidadeService;
import com.api.HbSolution.service.UsuarioDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = OportunidadeController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class OportunidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OportunidadeService oportunidadeService;

    @MockBean
    private LeadService leadService;

    @MockBean
    private AtividadeRepository atividadeRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getPipeline_retornaMapComOportunidadesPorEtapa() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(2L);
        usuario.setEmpresaId(11L);
        usuario.setRole("ADMIN");

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(3L);
        oportunidade.setTitulo("Pedido principal");
        oportunidade.setEtapa(EtapaOportunidade.PROSPECCAO);
        oportunidade.setStatus(StatusOportunidade.ABERTA);
        oportunidade.setValor(new BigDecimal("1500.00"));

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);
            when(oportunidadeService.findByEmpresaId(11L)).thenReturn(List.of(oportunidade));

            mockMvc.perform(get("/api/oportunidades/pipeline"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.PROSPECCAO[0].titulo").value("Pedido principal"))
                    .andExpect(jsonPath("$.PROSPECCAO[0].valor").value(1500.00));
        }
    }

    @Test
    void fecharOportunidade_quandoDadosValidos_retornaOportunidadeAtualizada() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(2L);
        usuario.setEmpresaId(11L);
        usuario.setRole("ADMIN");

        OportunidadeEntity oportunidade = new OportunidadeEntity();
        oportunidade.setId(4L);
        oportunidade.setTitulo("Novos contratos");
        oportunidade.setEtapa(EtapaOportunidade.FECHAMENTO_GANHO);
        oportunidade.setStatus(StatusOportunidade.GANHA);
        oportunidade.setValor(new BigDecimal("2500.00"));

        FecharOportunidadeRequest request = new FecharOportunidadeRequest();
        request.setStatus(StatusOportunidade.GANHA);
        request.setMotivo("Fechamento pelo cliente");
        request.setValorFinal(new BigDecimal("2500.00"));

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getUsuarioLogado).thenReturn(usuario);
            when(oportunidadeService.fecharOportunidade(eq(4L), eq(StatusOportunidade.GANHA),
                    eq("Fechamento pelo cliente"), eq(new BigDecimal("2500.00"))))
                    .thenReturn(oportunidade);

            mockMvc.perform(post("/api/oportunidades/4/fechar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.status").value("GANHA"));
        }
    }

    @Test
    void fecharOportunidade_quandoServiceLancaIllegalArgumentException_retornaApiError400() throws Exception {
        FecharOportunidadeRequest request = new FecharOportunidadeRequest();
        request.setStatus(StatusOportunidade.PERDIDA);
        request.setMotivo(null);
        request.setValorFinal(null);

        when(oportunidadeService.fecharOportunidade(eq(99L), eq(StatusOportunidade.PERDIDA), eq(null), eq(null)))
                .thenThrow(new IllegalArgumentException("Motivo é obrigatório para oportunidade perdida"));

        mockMvc.perform(post("/api/oportunidades/99/fechar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Motivo é obrigatório para oportunidade perdida"))
                .andExpect(jsonPath("$.path").value("/api/oportunidades/99/fechar"));
    }
}
